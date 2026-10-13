package com.theemrock.smooth

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso.pressBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ShellNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsEightCardsInRequiredTwoColumnOrder() {
        val modules = smoothModules()
        assertEquals(8, modules.size)

        modules.chunked(2).forEach { row ->
            scrollTo(row.first())
            val bounds = row.map { module ->
                composeRule.onNodeWithTag("module_${module.id}")
                    .assertIsDisplayed()
                    .fetchSemanticsNode().boundsInRoot
            }
            assertSameRow(bounds[0], bounds[1])
            assertTrue("left card must precede right card", bounds[0].left < bounds[1].left)
        }
    }

    @Test
    fun everyCardOpensItsExpectedScreenAndTopBarBackReturnsHome() {
        smoothModules().filterNot { it.id == "settings" }.forEach { module ->
            scrollTo(module)
            composeRule.onNodeWithTag("module_${module.id}").performClick()
            composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()
            composeRule.onNodeWithContentDescription(text(R.string.back)).performClick()
            composeRule.onNodeWithText(text(R.string.home_subtitle)).assertIsDisplayed()
        }

        scrollTo(smoothModules().last())
        composeRule.onNodeWithTag("module_settings").performClick()
        composeRule.onAllNodesWithText(text(R.string.settings_title)).assertCountEquals(1)
        composeRule.onNodeWithText(BuildConfig.VERSION_NAME).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(text(R.string.back)).performClick()
        composeRule.onNodeWithText(text(R.string.home_subtitle)).assertIsDisplayed()
    }

    @Test
    fun systemBackReturnsFromDetailToHomeWithoutClosingActivity() {
        val module = smoothModules().first()
        composeRule.onNodeWithTag("module_${module.id}").performClick()
        composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()

        pressBack()

        composeRule.onNodeWithText(text(R.string.home_subtitle)).assertIsDisplayed()
    }

    @Test
    fun recreationRetainsCurrentDestination() {
        val module = smoothModules().first { it.id == "reader" }
        scrollTo(module)
        composeRule.onNodeWithTag("module_${module.id}").performClick()
        composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()
    }

    @Test
    fun rotationRetainsCurrentDestination() {
        val module = smoothModules().first { it.id == "media" }
        composeRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        composeRule.waitUntil(5_000) {
            composeRule.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
        scrollTo(module)
        composeRule.onNodeWithTag("module_${module.id}").performClick()
        composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()

        try {
            composeRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            composeRule.waitUntil(5_000) {
                composeRule.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            }
            composeRule.onNodeWithText(placeholderText(module)).assertIsDisplayed()
        } finally {
            composeRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    @Test
    fun settingsShowsRealStateAndSurvivesRecreationThenSystemBack() {
        val settings = smoothModules().last()
        scrollTo(settings)
        composeRule.onNodeWithTag("module_settings").performClick()

        composeRule.onNodeWithText(text(R.string.app_name)).assertIsDisplayed()
        composeRule.onNodeWithText(BuildConfig.VERSION_NAME).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.check_updates)).performClick()
        composeRule.onNodeWithText(text(R.string.update_not_implemented)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.dialog_ok)).performClick()

        composeRule.activityRule.scenario.recreate()
        composeRule.onNodeWithText(BuildConfig.VERSION_NAME).assertIsDisplayed()
        pressBack()
        composeRule.onNodeWithText(text(R.string.home_subtitle)).assertIsDisplayed()
    }

    private fun scrollTo(module: ModuleItem) {
        composeRule.onNodeWithTag("module_grid")
            .performScrollToNode(hasTestTag("module_${module.id}"))
    }

    private fun placeholderText(module: ModuleItem): String =
        text(R.string.placeholder_desc, text(module.titleResId))

    private fun text(resourceId: Int, vararg args: Any): String =
        composeRule.activity.getString(resourceId, *args)

    private fun assertSameRow(left: Rect, right: Rect) {
        assertEquals(left.top, right.top, 1f)
    }
}
