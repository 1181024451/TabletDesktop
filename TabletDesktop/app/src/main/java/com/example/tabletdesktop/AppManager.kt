package com.example.tabletdesktop
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
class AppManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("desktop_prefs", Context.MODE_PRIVATE)
    fun getAllApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val whitelist = getWhitelist()
        return resolveInfos.map { info ->
            AppInfo(
                info.activityInfo.packageName,
                info.loadLabel(pm).toString(),
                info.loadIcon(pm),
                whitelist.contains(info.activityInfo.packageName) || whitelist.isEmpty()
            )
        }.sortedBy { it.appName }
    }
    fun getWhitelist(): Set<String> {
        return prefs.getString("whitelist", "")?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
    }
    fun saveWhitelist(packageNames: Set<String>) {
        prefs.edit().putString("whitelist", packageNames.joinToString(",")).apply()
    }
    fun getSelectedApps(): List<AppInfo> {
        val all = getAllApps()
        val wl = getWhitelist()
        return if (wl.isEmpty()) all else all.filter { wl.contains(it.packageName) }
    }
    fun toggleWhitelist(packageName: String, selected: Boolean) {
        val wl = getWhitelist().toMutableSet()
        if (selected) wl.add(packageName) else wl.remove(packageName)
        saveWhitelist(wl)
    }
}
