package com.example.tabletdesktop
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class AppAdapter(private val context: Context, private var appList: List<AppInfo>) :
    RecyclerView.Adapter<AppAdapter.AppViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.itemView.setOnClickListener {
            context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) }
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
    }
}
