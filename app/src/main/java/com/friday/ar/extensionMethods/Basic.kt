@file:Suppress("unused")

package com.friday.ar.extensionMethods

fun Any?.notNull(f: () -> Unit): Any? {
    if (this != null) {
        f()
    }
    return this
}

fun Any?.isNull(f: () -> Unit): Any? {
    if (this == null) {
        f()
    }
    return this
}