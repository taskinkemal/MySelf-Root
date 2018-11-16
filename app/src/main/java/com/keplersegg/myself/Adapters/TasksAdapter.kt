package com.keplersegg.myself.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.keplersegg.myself.Activities.MainActivity
import com.keplersegg.myself.Fragments.AddTaskFragment
import com.keplersegg.myself.Helper.HttpClient
import com.keplersegg.myself.Helper.ServiceMethods
import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.Entry
import com.keplersegg.myself.Room.Entity.TaskEntry
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class TasksAdapter(private val activity: MainActivity, private val day: Int) : RecyclerView.Adapter<TasksAdapter.DataObjectHolder>() {

    val items: ArrayList<TaskEntry> = ArrayList()

    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lytListItem: LinearLayout? = itemView.findViewById(R.id.lytListItem)
        val lblLabel: TextView = itemView.findViewById(R.id.lblLabel)
        val imgDone: ImageButton? = itemView.findViewById(R.id.imgDone)
        val imgPlus: ImageButton? = itemView.findViewById(R.id.imgPlus)
        val imgMinus: ImageButton? = itemView.findViewById(R.id.imgMinus)
        val txtValue: TextView? = itemView.findViewById(R.id.txtValue)
        val txtUnit: TextView? = itemView.findViewById(R.id.txtUnit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {

        val layout = if (viewType == 0) R.layout.list_item_task_boolean else R.layout.list_item_task_numeric
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return DataObjectHolder(itemView = view)
    }

    init {

        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        // object item based on the position
        val item = if (items.size > position) items[position] else null

        if (item != null) {

            var entry: Entry? = item.entry
            if (entry == null) {

                entry = Entry()
                entry.TaskId = item.task!!.Id
                entry.Day = day
                entry.Value = 0
                entry.ModificationDate = Date(System.currentTimeMillis())
                item.entry = entry

                doAsync {
                    activity.AppDB().entryDao().insert(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                uiThread { updateUi(holder, item) }
                }
            } else {
                updateUi(holder, item)
            }
        }

        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            if (item != null) {

                activity.NavigateFragment(true, AddTaskFragment.newInstance(item.task!!.Id))
                return@OnLongClickListener true
            }
            false
        })

        if (holder.itemViewType == 0) {

            holder.imgDone!!.setOnClickListener {
                val entry = item!!.entry
                entry!!.Value = if (entry.Value == 0) 1 else 0
                entry.ModificationDate = Date(System.currentTimeMillis())
                setTint(holder.imgDone, entry.Value == 1)

                doAsync {
                    activity.AppDB().entryDao().update(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                }
            }
        }
        else {

            holder.imgPlus!!.setOnClickListener {
                val entry = item!!.entry
                entry!!.Value++
                entry.ModificationDate = Date(System.currentTimeMillis())

                holder.txtValue!!.text = item.entry!!.Value.toString()

                doAsync {
                    activity.AppDB().entryDao().update(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                }
            }

            holder.imgMinus!!.setOnClickListener {
                val entry = item!!.entry
                if (entry!!.Value > 0)
                    entry.Value--
                entry.ModificationDate = Date(System.currentTimeMillis())

                holder.txtValue!!.text = item.entry!!.Value.toString()

                doAsync {
                    activity.AppDB().entryDao().update(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                }
            }
        }
    }

    private fun updateUi(holder: DataObjectHolder, item: TaskEntry) {

        holder.lblLabel.text = item.task!!.Label
        if (holder.itemViewType == 0) {
            setTint(holder.imgDone!!, item.entry!!.Value == 1)
        }
        else {
            holder.txtValue!!.text = item.entry!!.Value.toString()
            holder.txtUnit!!.text = item.task!!.Unit
        }
    }

    private fun setTint(imgDone: ImageButton, isChecked: Boolean) {

        val color = if (isChecked) R.color.colorAccent else android.R.color.darker_gray
        imgDone.setColorFilter(ContextCompat.getColor(activity, color),
                android.graphics.PorterDuff.Mode.SRC_IN)
    }

    override fun getItemId(position: Int): Long {

        return items[position].task!!.Id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.count() > position)
            items[position].task!!.DataType
        else 0
    }

    private fun uploadEntry(entry: Entry) {

        ServiceMethods.uploadEntry(activity, entry)
    }

    fun updateData(list: List<TaskEntry>) {

        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}