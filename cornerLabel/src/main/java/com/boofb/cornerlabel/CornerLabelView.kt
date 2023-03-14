package com.boofb.cornerlabel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.sqrt

/**
 * 自定义角标
 */
class CornerLabelView : View {

    //角标背景颜色
    private var mBgColor = Color.RED

    //背景圆角半径
    private var mCornerRadius = 0F

    //文本
    private var mText = "hello"

    //角标文本颜色
    private var mTextColor = Color.WHITE

    //角标文本字体大小
    private var mTextSize = dp2px(12F)

    //角标位置
    private var mLocationType = 0

    //文字距离底边距离
    private var mTextPaddingV = 0F

    //背景画笔
    private var mBgPaint: Paint? = null

    //文字画笔
    private var mTextPaint: Paint? = null

    //三角形
    private var mTriangle = true

    //文字基线
    private var mTextBaseLine = 0F

    //文字高度
    private var mTextHeight = 0

    //平方根2
    private val SQRT2 = sqrt(2.0)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {


        context?.obtainStyledAttributes(attrs, R.styleable.CornerLabelView, 0, 0)
            ?.let {
                mBgColor = it.getColor(R.styleable.CornerLabelView_clv_bg_color, mBgColor)
                mCornerRadius =
                    it.getDimension(R.styleable.CornerLabelView_clv_bg_corner_radius, mCornerRadius)
                mTextColor = it.getColor(R.styleable.CornerLabelView_clv_text_color, mTextColor)
                mTextSize = it.getDimension(R.styleable.CornerLabelView_clv_text_size, mTextSize)
                mTextPaddingV =
                    it.getDimension(R.styleable.CornerLabelView_clv_text_padding_v, mTextPaddingV)
                mLocationType = it.getInt(R.styleable.CornerLabelView_clv_location_type, 0)
                mText = it.getString(R.styleable.CornerLabelView_text) ?: ""
                mTriangle = it.getBoolean(R.styleable.CornerLabelView_clv_triangle, mTriangle)
                it.recycle()
            }
        Log.e("mCornerRadius", "$mCornerRadius")
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint?.style = Paint.Style.FILL
        mBgPaint?.color = mBgColor

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint?.color = mTextColor
        mTextPaint?.textSize = mTextSize
        mTextPaint?.textAlign = Paint.Align.CENTER

        measureText()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBg(canvas)
        drawText(canvas)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        measureText()
    }


    //测量文字
    private fun measureText() {
        mTextPaint?.let {
            val textRect = Rect()
            it.getTextBounds(mText, 0, mText.length, textRect)
            mTextHeight = textRect.height()
            val fontMetricsInt = it.fontMetrics
            mTextBaseLine = (measuredHeight - fontMetricsInt.top - fontMetricsInt.bottom) / 2F
        }
    }

    //绘写文字
    private fun drawText(canvas: Canvas) {
        canvas.save()
        canvas.rotate(90F * (mLocationType % 2) - 45, width / 2F, height / 2F)
        mTextPaint?.let {
            if (mLocationType < 2) {
                canvas.drawText(
                    mText,
                    width / 2F,
                    mTextBaseLine - (mTextHeight / 2F) - mTextPaddingV,
                    it
                )
            } else {
                canvas.drawText(
                    mText,
                    width / 2F,
                    mTextBaseLine + (mTextHeight / 2F) + mTextPaddingV,
                    it
                )
            }

        }
        canvas.restore()

    }

    //绘写背景
    private fun drawBg(canvas: Canvas) {

        val path = Path()
        if (mTriangle) {

            if (mCornerRadius == 0F) {
                path.lineTo(width.toFloat(), 0F)
                path.lineTo(0F, height.toFloat())
            } else {
                path.moveTo(0F, mCornerRadius)
                path.arcTo(
                    RectF(0F, 0F, mCornerRadius * 2, mCornerRadius * 2),
                    180F, 90F
                )
                path.rLineTo(width.toFloat() - mCornerRadius, 0F)
                path.rLineTo(-width.toFloat(), height.toFloat())
            }
            path.close()
        } else {
            val x = ((mTextHeight + mTextPaddingV * 2) * SQRT2).toFloat()
            path.moveTo(0F, height.toFloat())
            path.lineTo(height.toFloat(), 0F)
            path.rLineTo(-x, 0F)
            path.rLineTo(x - width.toFloat(), height.toFloat() - x)

            path.close()
        }


        canvas.save()
        canvas.rotate(90F * mLocationType, width / 2F, height / 2F)
        mBgPaint?.let {
            canvas.drawPath(path, it)
        }
        canvas.restore()
    }


    private fun dp2px(p: Float): Float {
        return p * resources.displayMetrics.density + 0.5f
    }

    private fun dp2px(p: Int): Int {
        return (p * resources.displayMetrics.density + 0.5).toInt()
    }

}

