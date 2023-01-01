package com.example.paintingapp.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.Interface.ViewOnClick
import com.example.paintingapp.R

class ToolsViewHolder: RecyclerView.ViewHolder {
    var icon: ImageView = itemView.findViewById(R.id.tools_icon)
    var name: TextView = itemView.findViewById(R.id.tools_name)
    private lateinit var viewOnClick: ViewOnClick
    fun setViewOnClick(viewOnClick: ViewOnClick){
        this.viewOnClick = viewOnClick
    }
    constructor(itemView: View)
            :super(itemView) {

        itemView.setOnClickListener {
            viewOnClick.onClick(adapterPosition)
        }
    }
}