@file:Suppress("unused")

package com.friday.ar.extensionMethods

fun <T> T?.notNull(block: T.() -> Unit): T? {
    if (this != null) {
        block()
    }
    return this
}

fun <T> T?.isNull(block: T?.() -> Unit): T? {
    if (this == null) {
        block()
    }
    return this
}