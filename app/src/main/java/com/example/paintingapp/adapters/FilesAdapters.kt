package com.example.paintingapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.Interface.ViewOnClick
import com.example.paintingapp.R
import com.example.paintingapp.ViewFileAct
import com.example.paintingapp.viewHolder.FileViewHolder
import java.io.File

class FilesAdapters(_mContext: Context, _fileList: List<File>) : RecyclerView.Adapter<FileViewHolder>() {
    private var mContext = _mContext
    private var fileList = _fileList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.row_file,parent,false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.imageView.setImageURI(Uri.fromFile(fileList[position]))
        holder.setViewOnClick(object : ViewOnClick {
            override fun onClick(pos: Int) {
                var intent = Intent(mContext, ViewFileAct::class.java)
                intent.data = Uri.fromFile(fileList[pos])
                mContext.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return fileList.size
    }
}