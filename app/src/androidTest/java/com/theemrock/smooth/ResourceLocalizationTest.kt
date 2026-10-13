package com.theemrock.smooth

import android.content.res.Configuration
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceLocalizationTest {
    @Test
    fun defaultAndTurkishResourcesContainExpectedVisibleCopy() {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val englishConfig = Configuration(base.resources.configuration).apply {
            setLocale(Locale.ENGLISH)
        }
        val turkishConfig = Configuration(base.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("tr"))
        }
        val english = base.createConfigurationContext(englishConfig).resources
        val turkish = base.createConfigurationContext(turkishConfig).resources

        assertEquals("Version", english.getString(R.string.app_version_label))
        assertEquals("Check for updates", english.getString(R.string.check_updates))
        assertEquals("Sürüm", turkish.getString(R.string.app_version_label))
        assertEquals("Güncellemeleri kontrol et", turkish.getString(R.string.check_updates))
        assertEquals(
            "Güncelleme işlevi henüz uygulanmadığından bu özellik aktif değildir.",
            turkish.getString(R.string.update_not_implemented)
        )
    }
}
