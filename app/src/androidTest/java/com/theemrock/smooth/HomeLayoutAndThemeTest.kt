package com.theemrock.smooth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.theemrock.smooth.ui.theme.SmoothTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeLayoutAndThemeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<TestHostActivity>()

    @Test
    fun allCardsRemainReachableWithoutTextClippingOnSmallScreenAtLargeFontScale() {
        composeRule.setContent {
            val currentDensity = LocalDensity.current
            androidx.compose.runtime.CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density, fontScale = 2f)
            ) {
                Box(Modifier.size(width = 320.dp, height = 480.dp)) {
                    SmoothTheme {
                        HomeScreen(modules = testModules(), onModuleClick = {})
                    }
                }
            }
        }

        testModules().forEach { module ->
            composeRule.onNodeWithTag("module_grid")
                .performScrollToNode(hasTestTag("module_${module.id}"))
            val cardBounds = composeRule.onNodeWithTag("module_${module.id}")
                .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            val labelBounds = composeRule.onNodeWithText(
                composeRule.activity.getString(module.titleResId)
            ).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            assertTrue("label top must remain inside card", labelBounds.top >= cardBounds.top)
            assertTrue("label bottom must remain inside card", labelBounds.bottom <= cardBounds.bottom)
        }
    }

    @Test
    fun darkThemeUsesRequiredCoreColors() {
        var background = Color.Unspecified
        var surface = Color.Unspecified
        var foreground = Color.Unspecified
        var accent = Color.Unspecified
        composeRule.setContent {
            SmoothTheme(darkTheme = true) {
                background = MaterialTheme.colorScheme.background
                surface = MaterialTheme.colorScheme.surface
                foreground = MaterialTheme.colorScheme.onBackground
                accent = MaterialTheme.colorScheme.primary
            }
        }
        composeRule.runOnIdle {
            assertEquals(Color(0xFF090B0D), background)
            assertEquals(Color(0xFF111418), surface)
            assertEquals(Color(0xFFF4F6F8), foreground)
            assertEquals(Color(0xFF19C3E6), accent)
        }
    }

    @Test
    fun lightThemeUsesRequiredCoreColors() {
        var background = Color.Unspecified
        var surface = Color.Unspecified
        var foreground = Color.Unspecified
        var accent = Color.Unspecified
        composeRule.setContent {
            SmoothTheme(darkTheme = false) {
                background = MaterialTheme.colorScheme.background
                surface = MaterialTheme.colorScheme.surface
                foreground = MaterialTheme.colorScheme.onBackground
                accent = MaterialTheme.colorScheme.primary
            }
        }
        composeRule.runOnIdle {
            assertEquals(Color(0xFFF5F7F8), background)
            assertEquals(Color(0xFFFFFFFF), surface)
            assertEquals(Color(0xFF111418), foreground)
            assertEquals(Color(0xFF087F99), accent)
        }
    }

    private fun testModules() = listOf(
        ModuleItem("agent", R.string.module_agent),
        ModuleItem("dialogue", R.string.module_dialogue),
        ModuleItem("reader", R.string.module_reader),
        ModuleItem("wiki", R.string.module_wiki),
        ModuleItem("nav", R.string.module_nav),
        ModuleItem("media", R.string.module_media),
        ModuleItem("reserved", R.string.module_reserved),
        ModuleItem("settings", R.string.module_settings)
    )
}
