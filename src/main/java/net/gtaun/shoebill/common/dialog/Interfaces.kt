/**
 * Copyright (C) 2014 MK124

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.shoebill.common.dialog

@FunctionalInterface
interface ConditionSupplier<out T> {
    operator fun get(condition: Boolean): T
}

fun <T> ConditionSupplier(handler: (Boolean) -> T) = object : ConditionSupplier<T> {
    override fun get(condition: Boolean): T {
        return handler(condition)
    }
}

@FunctionalInterface
interface DialogHandler {
    fun handle(dialog: AbstractDialog)
}

fun DialogHandler(handler: (AbstractDialog) -> Unit) = object : DialogHandler {
    override fun handle(dialog: AbstractDialog) {
        handler(dialog)
    }
}

@FunctionalInterface
interface DialogTextSupplier {
    operator fun get(dialog: AbstractDialog?): String
}

fun DialogTextSupplier(handler: (AbstractDialog?) -> String) = object : DialogTextSupplier {
    override fun get(dialog: AbstractDialog?): String {
        return handler(dialog)
    }
}