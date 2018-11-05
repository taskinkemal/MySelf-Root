package com.keplersegg.myself.Fragments


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_tasks.*

import com.keplersegg.myself.Adapters.TasksAdapter
import com.keplersegg.myself.Async.GetTasks
import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.TaskEntry
import java.util.*


class TasksFragment : MasterFragment() {

    private var tasks: MutableList<TaskEntry>? = null
    private var adapter: TasksAdapter? = null
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_tasks
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasks = ArrayList()
        adapter = TasksAdapter(activity, tasks, day)
        rcylTasks.adapter = adapter
        rcylTasks.layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_tasks)

        MyGetTasks().Execute(day)
    }

    private inner class MyGetTasks internal constructor() : GetTasks(this@TasksFragment.activity) {

        override fun OnSuccess(items: List<TaskEntry>) {

            tasks!!.clear()
            tasks!!.addAll(items)

            adapter!!.notifyDataSetChanged()
        }

        override fun OnError(errPhrase: String) {

            //adapter.notifyDataSetChanged();
        }

        override fun OnConnectionError() {

            //adapter.notifyDataSetChanged();
        }
    }

    companion object {

        fun newInstance(day: Int): TasksFragment {

            val fragment = TasksFragment()

            fragment.day = day

            return fragment
        }
    }
}
