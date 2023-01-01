package com.example.paintingapp.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.Interface.ToolsListener
import com.example.paintingapp.Interface.ViewOnClick
import com.example.paintingapp.MainActivity
import com.example.paintingapp.R
import com.example.paintingapp.model.ToolsItem
import com.example.paintingapp.viewHolder.ToolsViewHolder


class ToolsAdapters: RecyclerView.Adapter<ToolsViewHolder> {
    private var toolsItemsList: List<ToolsItem>
    private var selected = -1
    private var listener: ToolsListener
    constructor(toolsItemsList: List<ToolsItem>, listener: MainActivity){
        this.toolsItemsList=toolsItemsList
        this.listener=listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.tools_item, parent, false)
        return ToolsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolsViewHolder, position: Int) {
        holder.name.text = toolsItemsList[position].getName()
        holder.icon.setImageResource(toolsItemsList[position].getIcon())

        holder.setViewOnClick(object : ViewOnClick {
            override fun onClick(pos: Int) {
                selected = pos
                listener.onSelected(holder.name.text as String)
                notifyDataSetChanged()
            }
        })

        if (selected == position){
            holder.name.setTypeface(holder.name.typeface, Typeface.BOLD)
        }else{
            holder.name.typeface = Typeface.DEFAULT

        }
    }

    override fun getItemCount(): Int {
        return toolsItemsList.size
    }
}