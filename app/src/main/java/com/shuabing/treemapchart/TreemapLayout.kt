package com.shuabing.treemapchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

/**
 * treemap 有很多算法
 * 1.Aspect Ratio 最好的是 Squarified
 * Squarified
 * Slice-and-dice
 * 长 > 宽 时去水平方向进行下一步操作
 *
 * Pivot-by-middle
 * Pivot-by-size
 * Cluster
 *
 */
class TreeMapLayout : View {
    private var mid = 0
    private val areaItems = ArrayList<MapItem>()
    private val rectFillPaint = Paint()
    private val rectStrokePaint = Paint()
    private val colors = ArrayList<Int>()
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        rectFillPaint.style = Paint.Style.FILL
        rectStrokePaint.style = Paint.Style.STROKE
        rectStrokePaint.color = Color.WHITE
        rectStrokePaint.strokeWidth = 3f
        colors.add(Color.parseColor("#a1c6ef"))
        colors.add(Color.parseColor("#ddedfd"))
        colors.add(Color.parseColor("#91e0ab"))
        colors.add(Color.parseColor("#b4efc9"))
        colors.add(Color.parseColor("#ceefdb"))
        colors.add(Color.parseColor("#ddf6e7"))
        colors.add(Color.parseColor("#eaf7ef"))

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        for(i in areaItems.indices) {
            rectFillPaint.color = colors[i]
            canvas.drawRect(areaItems[i].bound, rectFillPaint)
            canvas.drawRect(areaItems[i].bound, rectStrokePaint)
        }
    }

    fun initTreeMap(items:ArrayList<Double>){
        areaItems.clear()

        val totalArea = height * width
        var sum = 0.0
        items.forEach {
            sum += it
        }
        for (i in items.indices) {
            areaItems.add(MapItem(totalArea * items[i] / sum  , i))
        }

        val treeMap = RectF(0f, 0f, width.toFloat(), height.toFloat())
        squarify(areaItems, 0, areaItems.size - 1, treeMap)
        invalidate()
    }


    private fun squarify(items:ArrayList<MapItem>, start:Int, end:Int, bounds:RectF){
        if(start > end) {
            return
        }

        if(start == end){
            items[start].setRectf(bounds)
        }

        mid = start
        while (mid < end){
            if(highestAspect(items, start, mid, bounds) > highestAspect(items, start, mid+1, bounds)){
                mid++
            } else {
                val newBounds = layoutRow(items, start, mid, bounds)
                squarify(items, mid+1, end, newBounds)
            }
        }
    }

    private fun highestAspect(items:ArrayList<MapItem>, start:Int, end:Int, bounds: RectF):Float{
        layoutRow(items, start, end, bounds)
        var max = 0f
        for (i in start .. end) {
            val current = aspectRatio(items[i].bound)
            if( current > max) {
                max = current
            }
        }
        return max
    }

    private fun layoutRow(items:ArrayList<MapItem>, start:Int, end:Int, bounds: RectF):RectF {
        val w = bounds.width()
        val h = bounds.height()
        val isHorizontal = w > h
        val total   =  w * h
        val rowSize = totalSize(items, start, end)
        val rowRatio = rowSize/total
        var offset = 0.0
        val boundW = bounds.right - bounds.left
        val boundH = bounds.bottom - bounds.top
        for (i in start.. end) {
            val r = RectF()
            val ratio = items[i].size / rowSize
            /*长大于宽就竖直切分 可以 是Aspect Ratio低*/
            if(isHorizontal) {
                r.left = bounds.left
                r.right = bounds.left + (boundW * rowRatio).toFloat()
                r.top = (bounds.top + boundH * offset).toFloat()
                r.bottom = (r.top + boundH * ratio).toFloat()
            } else {
                r.left = (bounds.left + boundW * offset).toFloat()
                r.right = r.left + (boundW * ratio).toFloat()
                r.top = bounds.top
                r.bottom = (bounds.top + boundH * rowRatio).toFloat()
            }
            items[i].setRectf(r)
            offset+=ratio
        }
        return if(isHorizontal) {
            val left = (bounds.left + boundW * rowRatio).toFloat()
            RectF(left, bounds.top, (left + boundW - boundW * rowRatio).toFloat(), bounds.top + boundH)
        } else {
            val top = (bounds.top + boundH * rowRatio).toFloat()
            RectF(bounds.left, top, bounds.left + boundW , (top + boundH - boundH * rowRatio).toFloat())
        }
    }

    private fun totalSize(items: ArrayList<MapItem>) :Double{
        return totalSize(items, 0, items.size - 1)
    }

    private fun totalSize(items: ArrayList<MapItem>, start: Int, end: Int):Double{
        var sum  = 0.0
        for (i in start .. end) {
            sum += items[i].size
        }
        return sum
    }

    /**
     * 正方形的长宽比是1
     * 长宽比接近1
     */
    private fun aspectRatio(rectF: RectF):Float{
        val w = rectF.width()
        val h = rectF.height()
        return max(w/h, h/w)

    }

}