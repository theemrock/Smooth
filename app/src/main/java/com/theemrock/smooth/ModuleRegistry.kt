package com.theemrock.smooth

import android.content.Context
import android.content.pm.PackageManager

data class ModuleDefinition(
    val id: String,
    val titleResId: Int,
    val packageName: String?
)

enum class ModuleStatus {
    INSTALLED,
    NOT_INSTALLED,
    NOT_CONFIGURED,
    NOT_LAUNCHABLE
}

object ModuleRegistry {
    val modules = listOf(
        ModuleDefinition("explorer", R.string.module_explorer, null),
        ModuleDefinition("media", R.string.module_media, "com.theemrock.smoothmedia"),
        ModuleDefinition("reader", R.string.module_reader, "com.theemrock.smoothreader"),
        ModuleDefinition("agent", R.string.module_agent, null),
        ModuleDefinition("hypertext", R.string.module_hypertext, null),
        ModuleDefinition("sensus", R.string.module_sensus, "com.theemrock.smooth.sensus")
    )

    fun getStatus(context: Context, module: ModuleDefinition): ModuleStatus {
        val packageName = module.packageName
            ?: return ModuleStatus.NOT_CONFIGURED

        val pm = context.packageManager

        try {
            pm.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return ModuleStatus.NOT_INSTALLED
        }

        return if (pm.getLaunchIntentForPackage(packageName) != null) {
            ModuleStatus.INSTALLED
        } else {
            ModuleStatus.NOT_LAUNCHABLE
        }
    }
}