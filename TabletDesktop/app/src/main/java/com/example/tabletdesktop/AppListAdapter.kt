package com.example.tabletdesktop
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class AppListAdapter(private var appList: List<AppInfo>, private val onToggle: (AppInfo, Boolean) -> Unit) :
    RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_settings, parent, false)
        return AppViewHolder(view)
    }
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.switch.isChecked = app.isSelected
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            app.isSelected = isChecked
            onToggle(app, isChecked)
        }
        holder.itemView.setOnClickListener {
            val newState = !holder.switch.isChecked
            holder.switch.isChecked = newState
            app.isSelected = newState
            onToggle(app, newState)
        }
    }
    override fun getItemCount(): Int = appList.size
    fun updateData(newList: List<AppInfo>) {
        this.appList = newList
        notifyDataSetChanged()
    }
    class AppViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.app_icon)
        val name: TextView = itemView.findViewById(R.id.app_name)
        val switch: Switch = itemView.findViewById(R.id.app_switch)
    }
}
