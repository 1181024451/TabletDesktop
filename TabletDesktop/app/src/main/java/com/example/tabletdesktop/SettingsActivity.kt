package com.example.tabletdesktop
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class SettingsActivity : AppCompatActivity() {
    private lateinit var adapter: AppListAdapter
    private lateinit var appManager: AppManager
    private lateinit var passwordHelper: PasswordHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        appManager = AppManager(this)
        passwordHelper = PasswordHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.app_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val allApps = appManager.getAllApps()
        adapter = AppListAdapter(allApps) { app, isSelected ->
            appManager.toggleWhitelist(app.packageName, isSelected)
            if (appManager.getWhitelist().isEmpty()) {
                Toast.makeText(this, "至少保留一个应用", Toast.LENGTH_SHORT).show()
                appManager.toggleWhitelist(app.packageName, true)
                loadAppList()
            }
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btn_set_password).setOnClickListener { showSetPasswordDialog() }
        findViewById<Button>(R.id.btn_clear_password).setOnClickListener {
            if (passwordHelper.hasPassword()) {
                AlertDialog.Builder(this)
                    .setTitle("清除密码")
                    .setMessage("确定清除？")
                    .setPositiveButton("确定") { _, _ ->
                        passwordHelper.clearPassword()
                        updatePasswordStatus()
                        Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            } else {
                Toast.makeText(this, "未设置密码", Toast.LENGTH_SHORT).show()
            }
        }
        updatePasswordStatus()
    }

    private fun loadAppList() {
        adapter.updateData(appManager.getAllApps())
    }

    private fun updatePasswordStatus() {
        val tv = findViewById<TextView>(R.id.tv_password_status)
        if (passwordHelper.isPasswordEnabled()) {
            tv.text = "状态：已启用密码保护"
            tv.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        } else {
            tv.text = "状态：未启用密码保护"
            tv.setTextColor(resources.getColor(android.R.color.darker_gray))
        }
    }

    private fun showSetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_password, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_new_password)
        val etConfirm = dialogView.findViewById<EditText>(R.id.et_confirm_password)
        AlertDialog.Builder(this)
            .setTitle("设置切换密码")
            .setView(dialogView)
            .setPositiveButton("确认") { _, _ ->
                val p = etPassword.text.toString()
                if (p.isEmpty() || p != etConfirm.text.toString()) {
                    Toast.makeText(this, "密码不一致或为空", Toast.LENGTH_SHORT).show()
                } else if (p.length < 4) {
                    Toast.makeText(this, "至少4位", Toast.LENGTH_SHORT).show()
                } else {
                    passwordHelper.setPassword(p)
                    updatePasswordStatus()
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
