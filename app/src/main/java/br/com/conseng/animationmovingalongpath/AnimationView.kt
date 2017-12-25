package br.com.conseng.animationmovingalongpath

import android.content.Context
import android.graphics.*
import android.os.Handler

import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class AnimationView : View {

    private var step = 0f
    private var xMouseDown = 0f
    private var yMouseDown = 0f
    private val PADDING = 80
    private val STROKE_WIDTH = 4.0f
    private val REDUCE_VALUE = 0.1f


    private var deltaX = 0f
    private var deltaY = 0f

    private var paint: Paint? = null
    private var paintText: Paint? = null

    internal var animPath: Path = Path()
    internal var pathMeasure: PathMeasure = PathMeasure()
    internal var pathLength: Float = 0.toFloat()

    private var players: List<PlayerDto>? = null
    private var positions: MutableList<PointDto>? = null

    private var autoRun = true

    constructor(context: Context) : super(context) {
        initAnimationView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAnimationView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAnimationView()
    }

    private fun initAnimationView() {
        paint = Paint()
        paint!!.setAntiAlias(true)
        paint!!.setDither(true)
        paint!!.setFilterBitmap(true)
        paint!!.setColor(Color.GRAY)
        paint!!.setStrokeWidth(STROKE_WIDTH)
        paint!!.setStyle(Paint.Style.STROKE)

        animPath = Path()
        players = ArrayList()
        positions = ArrayList()
        autoRun = true
        step = 0f

        paintText = Paint()
        paintText!!.setColor(Color.WHITE)
        paintText!!.setTextSize(30f)
        paintText!!.setTextAlign(Paint.Align.CENTER)
    }

    override fun onDraw(canvas: Canvas?) {
        val width = width.toFloat()     // Obtem a largura da View em pixels
        val height = height.toFloat()   // Obtem a altura da View em pixels

        val radius: Float = (width - PADDING * 2) / 2 - STROKE_WIDTH

        val center_x: Float = width / 2
        val center_y: Float = PADDING + radius

        val oval = RectF()
        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius)

        val ovalBottom = RectF()
        val centerYBottom = height - radius - PADDING
        ovalBottom.set(center_x - radius,
                centerYBottom - radius,
                center_x + radius,
                centerYBottom + radius)

        // -------------------------------------------------------------------------
        // ------------- define Path where images move along -----------------------
        // -------------------------------------------------------------------------
        animPath.reset()
        animPath.moveTo(PADDING + STROKE_WIDTH, height - radius - PADDING)
        animPath.lineTo(PADDING + STROKE_WIDTH, center_y)
        animPath.arcTo(oval, 180f, 180f)
        animPath.lineTo(PADDING.toFloat() + STROKE_WIDTH.toFloat() + radius * 2, height - radius - PADDING)
        animPath.arcTo(ovalBottom, 360f, 180f)

        pathMeasure = PathMeasure(animPath, false)
        pathLength = pathMeasure.length


        // -------------------------------------------------------------------------
        // ------------- Draw path -------------------------------------------------
        // -------------------------------------------------------------------------
        canvas!!.drawLine(PADDING + STROKE_WIDTH, center_y,
                PADDING + STROKE_WIDTH,
                height - radius - PADDING, paint)
        canvas.drawArc(oval, 180f, 180f, false, paint)
        canvas.drawArc(ovalBottom, 360f, 180f, false, paint)
        canvas.drawLine(PADDING.toFloat() + STROKE_WIDTH.toFloat() + radius * 2, center_y,
                PADDING.toFloat() + STROKE_WIDTH.toFloat() + radius * 2,
                height - radius - PADDING, paint)

        for (i in players!!.indices) {
            val player = players!![i]
            // --------------------------------------------
            // --------- Draw object ----------------------
            // --------------------------------------------
            if (player.distance <= pathLength) {
                pathMeasure.getPosTan(player.distance, player.pos, player.tan)

                player.matrix.reset()
                val degrees = (Math.atan2(player.tan[1].toDouble(), player.tan[0].toDouble()) * 0.0 / Math.PI).toFloat()
                player.matrix.postRotate(degrees, player.offsetX, player.offsetY)
                player.matrix.postTranslate(player.pos[0] - player.offsetX, player.pos[1] - player.offsetY)

                if (player.bm != null) {
                    canvas.drawBitmap(player.bm, player.matrix, null)
                }

                player.distance += step
            } else {
                player.distance %= pathLength
            }

            if (player.distance < 0) {
                player.distance = pathLength + player.distance % pathLength
            }
        }

        if (autoRun) {
            reduceDistance()
            invalidate()
        }
    }

    internal var timerHandlerAuto = Handler()
    internal var timerRunnableAuto: Runnable = Runnable { reduceDistance() }

    private fun reduceDistance() {
        if (0 <= Math.abs(step - REDUCE_VALUE - 0.01f) && Math.abs(step - REDUCE_VALUE) <= REDUCE_VALUE + 0.01f) {
            step = 0f
            timerHandlerAuto.removeCallbacks(timerRunnableAuto)
            autoRun = false
        } else {
            if (step > 0) {
                step -= REDUCE_VALUE
            } else {
                step += REDUCE_VALUE
            }
            timerHandlerAuto.postDelayed(timerRunnableAuto, 1000)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xMouseDown = event.x
                yMouseDown = event.y
                //                autoRun = false;
                positions!!.clear()

                val point = PointDto()
                point.x = event.x
                point.y = event.y
                positions!!.add(point)

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val point1 = PointDto()
                point1.x = event.x
                point1.y = event.y
                positions!!.add(point1)
            }
            MotionEvent.ACTION_UP -> {
                autoRun = true

                val x = event.x
                val y = event.y

                if (x - xMouseDown == 0f && Math.abs(y - yMouseDown) < 4 || y - yMouseDown == 0f && Math.abs(x - xMouseDown) < 4) {
                    for (i in players!!.indices) {
                        val xOfYourBitmap = players!![i].pos[0] - Constants.PLAYER_SIZE / 2
                        val yOfYourBitmap = players!![i].pos[1] - Constants.PLAYER_SIZE / 2
                        if (x >= xOfYourBitmap && x < xOfYourBitmap + Constants.PLAYER_SIZE
                                && y >= yOfYourBitmap && y < yOfYourBitmap + Constants.PLAYER_SIZE) {
                            autoRun = false
                            // handle click event
                            Toast.makeText(context, "Clicked: " + ((i + 1).toString()), Toast.LENGTH_SHORT).show()
                            break
                        }
                    }
                }
                return false
            }
            else -> return false
        }

        for (i in players!!.indices) {
            if (positions!!.size >= 2) {
                deltaX = positions!![positions!!.size - 1].x - positions!![positions!!.size - 2].x
                deltaY = positions!![positions!!.size - 1].y - positions!![positions!!.size - 2].y

                if (i >= 1) {
                    val currentDistance = players!![i - 1].distance + Constants.PLAYER_DISTANT
                    players!![i].distance = currentDistance % pathLength
                }
            }

            if (players!![i].distance < 0) {
                players!![i].distance = pathLength + players!![i].distance % pathLength
            }
        }

        //----------- drag on right side ----------------------
        if (event.x > width / 2) {
            deltaY = -deltaY
        }
        step = -deltaY

        invalidate()
        return true
    }

    fun setPlayers(players: List<PlayerDto>) {
        this.players = players
        invalidate()
    }
}