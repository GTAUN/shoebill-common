package net.gtaun.shoebill.common

import net.gtaun.shoebill.Shoebill
import net.gtaun.shoebill.entities.Destroyable
import net.gtaun.util.event.EventManager
import net.gtaun.util.event.EventManagerNode

/**
 * Created by marvin on 14.11.16 in project shoebill-common.
 * Copyright (c) 2016 Marvin Haschker. All rights reserved.
 */

@AllOpen
class LifecycleHolder<T> @JvmOverloads
constructor(eventManager: EventManager = Shoebill.get().eventManager) : Destroyable {

    protected val eventManagerNode: EventManagerNode = eventManager.createChildNode()
    private val lifecycleObjects = mutableMapOf<T, MutableList<LifecycleObject>>()
    private val lifecycleFactories = mutableMapOf<Class<out LifecycleObject>,
            LifecycleFactory<T, LifecycleObject>>()

    fun <B : LifecycleObject> registerClass(lifecycleObject: Class<B>, factory: LifecycleFactory<T, B>) {
        lifecycleFactories.put(lifecycleObject, factory)
        lifecycleObjects.forEach { buildObject(it.key, lifecycleObject) }
    }

    fun <B : LifecycleObject> unregisterClass(lifecycleObject: Class<B>) {
        lifecycleObjects.forEach { destroyObject(it.key, lifecycleObject) }
        lifecycleFactories.remove(lifecycleObject)
    }

    @Suppress("UNCHECKED_CAST")
    fun <B : LifecycleObject> getObject(input: T, clazz: Class<B>): B? {
        val objects = lifecycleObjects[input] ?: return null
        val lifecycleObject = objects.firstOrNull { it.javaClass == clazz } ?: return null
        return lifecycleObject as B
    }

    fun <B : LifecycleObject> getObjects(clazz: Class<B>): List<B> {
        return lifecycleObjects.filter { it.value.filter { it.javaClass == clazz }.count() > 0 }
                .map { it.value }
                .first()
                .map { it as B }
    }

    fun buildObjects(input: T) {
        val list = lifecycleFactories.map {
            val obj = it.value.create(input)
            obj.init()
            return@map obj
        }.toMutableList()
        lifecycleObjects.put(input, list)
    }

    fun <B : LifecycleObject> buildObject(input: T, clazz: Class<B>) {
        val factory = lifecycleFactories.filter { it.key == clazz }.map { it.value }.firstOrNull() ?: return
        val playerList = lifecycleObjects[input] ?: return
        val obj = factory.create(input)
        obj.init()
        playerList.add(obj)
    }

    fun destroyObjects(input: T) {
        val list = lifecycleObjects[input] ?: return
        list.forEach { it.destroy() }
        lifecycleObjects.remove(input)
    }

    fun <B : LifecycleObject> destroyObject(input: T, clazz: Class<B>) {
        val playerList = lifecycleObjects[input] ?: return
        val obj = playerList.firstOrNull { it.javaClass == clazz } ?: return
        obj.destroy()
        playerList.remove(obj)
    }

    override val isDestroyed: Boolean
        get() = eventManagerNode.isDestroyed

    override fun destroy() = eventManagerNode.destroy()

    @FunctionalInterface
    interface LifecycleFactory<in B, out T : LifecycleObject> {
        fun create(input: B): T
    }

}