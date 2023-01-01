package com.example.paintingapp

import android.Manifest
import android.R.attr
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapp.Interface.ToolsListener
import com.example.paintingapp.adapters.ToolsAdapters
import com.example.paintingapp.common.Common
import com.example.paintingapp.model.ToolsItem
import com.example.paintingapp.widget.PaintView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), ToolsListener {
    private val REQUEST_PERMISSION = 100
    private val PICK_IMAGE = 1000
    lateinit var mPaintView: PaintView
    var colorBackground: Int=0
    var colorBrush: Int=0
    var brushSize: Int =0
    var eraserSize: Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTools()
    }
    private fun initTools(){
        colorBackground = Color.WHITE
        colorBrush = Color.BLACK

        eraserSize=12
        brushSize=12
        mPaintView = findViewById(R.id.paint_view)

        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_tools)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        var toolsAdapter = ToolsAdapters(loadTools(), this)
        recyclerView.adapter=toolsAdapter
    }
    private fun loadTools() : List<ToolsItem>{
        var result = mutableListOf<ToolsItem>()
        result.add(ToolsItem(R.drawable.ic_baseline_brush_24, Common.BRUSH))
        result.add(ToolsItem(R.drawable.eraser,Common.ERASER))
        result.add(ToolsItem(R.drawable.ic_baseline_image_24,Common.IMAGE))
        result.add(ToolsItem(R.drawable.ic_baseline_palette_24,Common.COLORS))
        result.add(ToolsItem(R.drawable.paint_white,Common.BACKGROUND))
        result.add(ToolsItem(R.drawable.ic_baseline_undo_24,Common.RETURN))
        return result
    }
    fun finishPaint(view: View){

    }
    fun shareApp(view: View){
        var intent = Intent (Intent.ACTION_SEND)
        intent.setType("text/plain")
        var bodyText = "http://play.google.com/store/apps/details?id=$packageName"
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name))
        intent.putExtra(Intent.EXTRA_TEXT,bodyText)
        startActivity(Intent.createChooser(intent, "share this"))
    }
    fun showFiles(view: View){
        startActivity(Intent(this,ListFilesAct::class.java))
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun saveFiles(view: View){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(Array(1){
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_PERMISSION)
        }else{
            saveBitmap()
        }
    }
    private fun saveBitmap(){
        var bitmap = mPaintView.getBitmap()
        var file_name = "${UUID.randomUUID()}.png"
        var folder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+File.separator+getString(R.string.app_name))
        if (!folder.exists()){
            folder.mkdir()
        }
        try{
            var fileOutputStream = FileOutputStream(folder.toString()+File.separator+file_name)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            Toast.makeText(this, "picture saved", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: kotlin.Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            saveBitmap()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSelected(name: String) {
        when(name){
            Common.BRUSH->{
                mPaintView.disableEraser()
                showDialogSize(false)
            }
            Common.ERASER->{
                mPaintView.enableEraser()
                showDialogSize(true)
            }
            Common.RETURN->{
                mPaintView.returnLastAction()
            }
            Common.BACKGROUND->{
                updateColor(name)
            }
            Common.COLORS->{
                updateColor(name)
            }
            Common.IMAGE->{
                getImage()
            }
        }
    }
    private fun getImage(){
        var intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === PICK_IMAGE && attr.data != null && resultCode === RESULT_OK) {
            val pickedImage: Uri? = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(pickedImage!!, filePath, null, null, null)
            cursor?.moveToFirst()
            val imagePath = cursor?.getString(cursor.getColumnIndexOrThrow(filePath[0]))
            Log.i("tag",imagePath.toString())
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(imagePath,options)
            mPaintView.setImage(bitmap)
            cursor?.close()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun showDialogSize(isEraser: Boolean){
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        var view: View = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null, false)
        var toolsSelected: TextView = view.findViewById(R.id.status_tools_selected)
        var statusSize:TextView = view.findViewById(R.id.status_size)
        var ivTools: ImageView = view.findViewById(R.id.iv_tools)
        var seekBar: SeekBar = view.findViewById(R.id.seekbar_size)
        seekBar.max = 99

        if (isEraser){
            toolsSelected.text = "Eraser Size"
            ivTools.setImageResource(R.drawable.eraser_black)
            statusSize.text = "Selected Size: $eraserSize"
        }else{
            toolsSelected.text = "Brush Size"
            ivTools.setImageResource(R.drawable.ic_baseline_black_brush_24)
            statusSize.text = "Selected Size: $brushSize"
        }
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(isEraser){
                    eraserSize = progress+1
                    statusSize.text = "Selected Size: $eraserSize"
                    mPaintView.setSizeEraser(eraserSize)
                }else{
                    brushSize = progress+1
                    statusSize.text = "Selected Size: $brushSize"
                    mPaintView.setSizeBrush(brushSize)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        builder.setPositiveButton("OK") { dialog, _ -> dialog?.dismiss() }
        builder.setView(view)
        builder.show()
    }
    private fun updateColor(name: String){
        var color = if (name == Common.BACKGROUND){
            colorBackground
        }else{
            colorBrush
        }
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .initialColor(color)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("OK", object: ColorPickerClickListener{
                override fun onClick(
                    d: DialogInterface?,
                    lastSelectedColor: Int,
                    allColors: kotlin.Array<out Int>?,
                ) {
                    if (name == Common.BACKGROUND){
                        colorBackground = lastSelectedColor
                        mPaintView.setColorBackground(colorBackground)
                    }else{
                        colorBrush = lastSelectedColor
                        mPaintView.setBrushColor(colorBrush)
                    }
                }
            }).setNegativeButton("CANCEL", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {

                }
            }).build().show()
    }
}