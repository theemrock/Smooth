package com.theemrock.smooth

import android.content.Context
import android.content.ActivityNotFoundException

enum class LaunchResult {
    LAUNCHED,
    NOT_INSTALLED,
    NOT_CONFIGURED,
    NOT_LAUNCHABLE,
    FAILED
}

object ModuleLauncher {

    fun launch(context: Context, module: ModuleDefinition): LaunchResult {
        val packageName = module.packageName
            ?: return LaunchResult.NOT_CONFIGURED

        val status = ModuleRegistry.getStatus(context, module)

        when (status) {
            ModuleStatus.NOT_CONFIGURED -> return LaunchResult.NOT_CONFIGURED
            ModuleStatus.NOT_INSTALLED -> return LaunchResult.NOT_INSTALLED
            ModuleStatus.NOT_LAUNCHABLE -> return LaunchResult.NOT_LAUNCHABLE
            ModuleStatus.INSTALLED -> Unit
        }

        val intent = context.packageManager
            .getLaunchIntentForPackage(packageName)
            ?: return LaunchResult.NOT_LAUNCHABLE

        return try {
            context.startActivity(intent)
            LaunchResult.LAUNCHED
        } catch (e: ActivityNotFoundException) {
            LaunchResult.NOT_LAUNCHABLE
        } catch (e: SecurityException) {
            LaunchResult.FAILED
        }
    }
}
