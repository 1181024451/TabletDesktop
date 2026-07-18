package com.example.tabletdesktop
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
class LauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppAdapter
    private lateinit var appManager: AppManager
    private lateinit var passwordHelper: PasswordHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        appManager = AppManager(this)
        passwordHelper = PasswordHelper(this)

        recyclerView = findViewById(R.id.app_grid)
        findViewById<Button>(R.id.btn_switch_desktop).setOnClickListener { showSwitchDialog() }
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        recyclerView.layoutManager = GridLayoutManager(this, 4)
        adapter = AppAdapter(this, appManager.getSelectedApps())
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData(appManager.getSelectedApps())
    }

    private fun showSwitchDialog() {
        if (passwordHelper.isPasswordEnabled()) {
            showPasswordDialog()
        } else {
            switchToSystemDesktop()
        }
    }

    private fun showPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)
        AlertDialog.Builder(this)
            .setTitle("输入密码")
            .setView(dialogView)
            .setPositiveButton("确认") { _, _ ->
                if (passwordHelper.verifyPassword(etPassword.text.toString())) {
                    switchToSystemDesktop()
                } else {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun switchToSystemDesktop() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            val target = resolveInfos.firstOrNull { it.activityInfo.packageName != packageName }
            if (target != null) {
                val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                i.setClassName(target.activityInfo.packageName, target.activityInfo.name)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            } else {
                startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "切换失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "请使用「切换到系统桌面」按钮", Toast.LENGTH_SHORT).show()
    }
}
