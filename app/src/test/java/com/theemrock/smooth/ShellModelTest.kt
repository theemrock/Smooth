package com.theemrock.smooth

import org.junit.Assert.assertEquals
import org.junit.Test

class ShellModelTest {
    @Test
    fun modulesHaveRequiredOrderAndDestinations() {
        assertEquals(
            listOf("explorer", "media", "reader", "agent", "hypertext", "sensus"),
            smoothModules().map(ModuleItem::id)
        )
    }

    @Test
    fun placeholderRouteCarriesSelectedModuleId() {
        assertEquals("placeholder/reader", Screen.Placeholder.createRoute("reader"))
    }
}
