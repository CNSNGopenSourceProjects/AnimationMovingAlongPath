package br.com.conseng.animationmovingalongpath

import android.content.res.Resources
import android.graphics.*
import android.media.ThumbnailUtils

class PlayerDto {
    var bm: Bitmap?
    var originalImage: Bitmap?
    var offsetX: Float
    var offsetY: Float
    var distance: Float
    var pos = FloatArray(2)
    var tan = FloatArray(2)
    var matrix = Matrix()

    constructor() {
        this.offsetX = 0.toFloat()
        this.offsetY = 0.toFloat()
        this.distance = 0.toFloat()
        this.bm = null
        this.originalImage = null
    }

    constructor(res: Resources, imagem: Int, distance: Float, offsetX: Float, offsetY: Float) {
        this.offsetX = offsetX
        this.offsetY = offsetY
        this.distance = distance
        this.originalImage = BitmapFactory.decodeResource(res, imagem)
        this.bm = getCircleBitmap(originalImage)
    }

    fun loadBitmapFromResource(res: Resources, imagem: Int) {
        this.originalImage = BitmapFactory.decodeResource(res, imagem)
        this.bm = getCircleBitmap(originalImage)
    }

    private fun getCircleBitmap(bm: Bitmap?): Bitmap?
    {
        if(null == bm) return null

        val bitmap = ThumbnailUtils.extractThumbnail(bm, Constants.PLAYER_SIZE.toInt(), Constants.PLAYER_SIZE.toInt())

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)

        val color = 0xffff0000.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)

        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.toFloat()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }
}
