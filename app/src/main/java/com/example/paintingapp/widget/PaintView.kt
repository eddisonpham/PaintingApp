package com.example.paintingapp.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private lateinit var btmBackground: Bitmap
    private lateinit var btmView: Bitmap
    private lateinit var image: Bitmap
    private var leftImage = 50F
    private var topImage = 50F
    private var mPaint = Paint()
    private var mPath = Path()
    private var colorBackground:Int=0
    private var sizeBrush:Int=0
    private var sizeEraser:Int=0
    private var mX: Float=0F
    private var mY: Float=0F
    private lateinit var mCanvas: Canvas
    private val DIFFERENCE_SPACE = 4
    private var listAction = ArrayList<Bitmap>()

    init{
        sizeEraser = 12
        sizeBrush = 12
        colorBackground= Color.WHITE

        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = toPx(sizeBrush)
    }
    private fun toPx(sizeBrush: Int):Float{
        return sizeBrush*(resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        btmBackground = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        btmView = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        mCanvas=Canvas(btmView)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(colorBackground)
        canvas?.drawBitmap(btmBackground, 0F, 0F, null)
        if (::image.isInitialized)
            canvas?.drawBitmap(image,leftImage,topImage,null)
        canvas?.drawBitmap(btmView,0F,0F,null)
    }
    fun setColorBackground(color:Int){
        colorBackground=color
        invalidate()
    }
    fun setBrushColor(color:Int){
        mPaint.color = color
    }
    fun setSizeEraser(s:Int){
        sizeEraser = s
        mPaint.strokeWidth = toPx(sizeEraser)
    }
    fun setSizeBrush(s:Int){
        sizeBrush = s
        mPaint.strokeWidth = toPx(sizeBrush)
    }
    fun enableEraser(){
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    fun disableEraser(){
        mPaint.xfermode = null
        mPaint.shader = null
        mPaint.maskFilter = null
    }
    fun addLastAction(bitmap:Bitmap){
        listAction.add(bitmap)
    }
    fun returnLastAction(){
        if (listAction.size>0){
            listAction.removeAt(listAction.size - 1)
            btmView = if (listAction.size>0){
                listAction[listAction.size-1]
            }else{
                Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            }
            mCanvas = Canvas(btmView)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var x: Float = event!!.x
        var y: Float = event!!.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->touchStart(x,y)
            MotionEvent.ACTION_MOVE->touchMove(x,y)
            MotionEvent.ACTION_UP->touchUp()
        }
        return true
    }
    private fun touchStart(x:Float, y:Float){
        mPath.moveTo(x,y)
        mX = x
        mY = y
    }
    private fun touchMove(x:Float, y:Float){
        var dx = abs(x-mX)
        var dy = abs(y-mY)
        if (dx >= DIFFERENCE_SPACE || dy >= DIFFERENCE_SPACE){
            mPath.quadTo(x,y,(x+mX)/2,(y+mY)/2)
            mY = y
            mX = x
            mCanvas.drawPath(mPath,mPaint)
            invalidate()
        }
    }
    private fun touchUp(){
        mPath.reset()
    }
    fun getBitmap():Bitmap{
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        var bitmap:Bitmap = Bitmap.createBitmap(this.drawingCache)
        this.isDrawingCacheEnabled = false
        return bitmap
    }
    fun setImage(bitmap: Bitmap){
        image = Bitmap.createScaledBitmap(bitmap, width/2,height/2,true)
        invalidate()
    }
}
