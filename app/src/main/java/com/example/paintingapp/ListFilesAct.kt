package com.example.paintingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.adapters.FilesAdapters
import com.example.paintingapp.common.Common
import java.io.File


class ListFilesAct : AppCompatActivity() {
    private lateinit var fileList: MutableList<File>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_files)
        initToolbar()
        initViews()
    }
    private fun initToolbar(){
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Pictures"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initViews(){
        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_files)
        recyclerView.setHasFixedSize(true)
        var gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager=gridLayoutManager
        fileList = loadFiles()
        var filesAdapters = FilesAdapters(this,loadFiles())
        recyclerView.adapter=filesAdapters
    }
    private fun loadFiles ():MutableList<File>{
        var inFiles = ArrayList<File>()
        var parenDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+File.separator+getString(R.string.app_name))
        var files: Array<out File> = parenDir.listFiles()
        for (file:File in files){
            if (file.name.endsWith(".png")){
                inFiles.add(file)
            }
        }
        var textView = findViewById<TextView>(R.id.status_empty)
        if(files.isNotEmpty()){
            textView.visibility = View.GONE
        }else{
            textView.visibility = View.VISIBLE
        }
        return inFiles
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId
        if (id == android.R.id.home){
            finish()
        }
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(item.title == Common.DELETE) {
            deleteFile(item.order)
            initViews()
        }
        return true
    }

    private fun deleteFile(order: Int) {
        fileList[order].delete()
        fileList.removeAt(order)
    }
}