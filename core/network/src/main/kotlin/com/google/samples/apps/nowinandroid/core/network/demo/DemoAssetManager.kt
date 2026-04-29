package com.google.samples.apps.nowinandroid.core.network.demo

import java.io.InputStream

fun interface DemoAssetManager {
    fun open(fileName: String): InputStream
}
