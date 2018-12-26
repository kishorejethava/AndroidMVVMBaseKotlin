package com.pr0gramm.app.util

import android.graphics.Bitmap
import android.util.Log
import androidx.collection.LruCache
import androidx.core.graphics.BitmapCompat
import com.squareup.picasso.Cache

/**
 * This is a better cache for [com.squareup.picasso.Picasso].
 * It caches only small images (for the feed).
 *
 * This should prevent further out of memory errors.
 */
class GuavaPicassoCache private constructor(maxSize: Int) : Cache {


    private val cache = object : LruCache<String, Bitmap>(maxSize) {
        override fun sizeOf(key: String, value: Bitmap): Int = bitmapByteCount(value)
    }

    init {
        Log.i("test", "Test ${ "Initializing cache with about ${maxSize / (1024 * 1024)}mb"}")
    }

    private fun bitmapByteCount(bitmap: Bitmap): Int {
        return BitmapCompat.getAllocationByteCount(bitmap)
    }

    override fun get(key: String): Bitmap? {
        return cache.get(key)
    }

    override fun set(key: String, bitmap: Bitmap) {
        if (bitmapByteCount(bitmap) <= MAX_CACHE_ITEM_SIZE) {
            cache.put(key, bitmap)
        }
    }

    override fun size(): Int {
        return cache.size()
    }

    override fun maxSize(): Int {
        return cache.maxSize()
    }

    override fun clear() {
        cache.evictAll()
    }

    override fun clearKeyUri(keyPrefix: String) {
        cache.snapshot().keys.filter { it.startsWith(keyPrefix) }.forEach { cache.remove(it) }
    }

    companion object {
        private const val MAX_CACHE_ITEM_SIZE = (128 * 128 * 4).toLong()

        @JvmStatic
        fun defaultSizedGuavaCache(): GuavaPicassoCache {
            val maxMemory = (Runtime.getRuntime().maxMemory() / 20L).toInt()
                    .coerceIn(minimumValue = 2 * 1024 * 1024, maximumValue = 6 * 1024 * 1024)

            return GuavaPicassoCache(maxMemory)
        }
    }
}

/*UserCase*/

/* Init to Applicaiton leval or MainActivity
private fun initializePicasso() {
    val downloader = OkHttp3Downloader(this)
    val picasso = Picasso.Builder(this)
            .downloader(downloader).memoryCache(GuavaPicassoCache.defaultSizedGuavaCache())
            .build()
    try {
        Picasso.setSingletonInstance(picasso)
    } catch (ignored: IllegalStateException) {
        // instance was already set
    }
}*/
