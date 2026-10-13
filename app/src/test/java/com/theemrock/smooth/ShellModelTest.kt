package com.theemrock.smooth

import org.junit.Assert.assertEquals
import org.junit.Test

class ShellModelTest {
    @Test
    fun modulesHaveRequiredOrderAndDestinations() {
        assertEquals(
            listOf("agent", "dialogue", "reader", "wiki", "nav", "media", "reserved", "settings"),
            smoothModules().map(ModuleItem::id)
        )
    }

    @Test
    fun placeholderRouteCarriesSelectedModuleId() {
        assertEquals("placeholder/reader", Screen.Placeholder.createRoute("reader"))
    }
}
