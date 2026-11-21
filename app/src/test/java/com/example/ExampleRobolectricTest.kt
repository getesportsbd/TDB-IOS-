package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BuiltInCatalog
import com.example.data.WallpaperPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name and package id are configured correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TDB iOS Experience", appName)
        assertEquals("com.tdb.ios.experience", context.packageName)
    }

    @Test
    fun `home shortcuts and dock catalog are populated`() {
        assertTrue(BuiltInCatalog.homeShortcuts.size >= 10)
        assertEquals(4, BuiltInCatalog.dockShortcuts.size)
        assertTrue(WallpaperPreset.AURORA.drawableRes != null)
    }
}
