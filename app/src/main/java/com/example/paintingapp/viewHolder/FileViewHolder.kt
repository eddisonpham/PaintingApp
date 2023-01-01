package com.example.paintingapp.viewHolder

import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.Interface.ViewOnClick
import com.example.paintingapp.R
import com.example.paintingapp.common.Common

class FileViewHolder: RecyclerView.ViewHolder {
    var imageView: ImageView = itemView.findViewById(R.id.image)
    private lateinit var viewOnClick: ViewOnClick
    fun setViewOnClick(viewOnClick: ViewOnClick){
        this.viewOnClick = viewOnClick
    }
    constructor(itemView: View) :super(itemView){
        itemView.setOnClickListener{
            viewOnClick.onClick(adapterPosition)
        }
        itemView.setOnCreateContextMenuListener(object: View.OnCreateContextMenuListener {
            override fun onCreateContextMenu(
                menu: ContextMenu?,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo?,
            ) {
                menu?.add(0,0,adapterPosition, Common.DELETE)
            }

        })
    }
}