package com.shuabing.treemapchart

import android.graphics.RectF

class MapItem {
    var size:Double = 0.0
    var order :Int = 0
    var bound:RectF = RectF()
    fun setRectf(rectF: RectF) {
        bound.left = rectF.left
        bound.right = rectF.right
        bound.top = rectF.top
        bound.bottom = rectF.bottom
    }
    constructor(size:Double, order: Int){
        this.size = size
        this.order = order
    }


}