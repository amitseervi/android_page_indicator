package com.amit.pageindicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlin.math.max

private const val DEFAULT_VISIBLE_INDICATORS = 5

class HorizontalPageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPageCount: Int = 0
    private var mIndicatorSize: Int = 0
    private var mIndicatorRadius: Int = 0
    private var visibleIndicatorCount: Int = 0
    private var mIndicatorPadding: Int = 0
    private var mStrokeWidth: Int = 0
    private val mActivePaint = Paint()
    private val mInactivePaint = Paint()
    private var selectedIndex = 0

    init {
        val defaultIndicatorPadding =
            context.resources.getDimensionPixelSize(R.dimen.default_indicator_padding)
        val defaultIndicatorRadius =
            context.resources.getDimensionPixelSize(R.dimen.default_indicator_radius)
        val defaultStrokeWidth =
            context.resources.getDimensionPixelSize(R.dimen.default_indicator_stroke_width)
        val defaultIndicatorActiveColor =
            ContextCompat.getColor(context, R.color.default_indicator_active_color)
        val defaultIndicatorInActiveColor =
            ContextCompat.getColor(context, R.color.default_indicator_inactive_color)
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalPageIndicator,
            defStyleAttr,
            defStyleAttr
        )
        mPageCount = typedArray.getInt(
            R.styleable.HorizontalPageIndicator_pageCount,
            0
        )

        mIndicatorRadius = typedArray.getDimensionPixelSize(
            R.styleable.HorizontalPageIndicator_indicatorRadius,
            defaultIndicatorRadius
        )

        mIndicatorSize = 2 * mIndicatorRadius

        visibleIndicatorCount = typedArray.getInt(
            R.styleable.HorizontalPageIndicator_visibleIndicatorCount,
            DEFAULT_VISIBLE_INDICATORS
        )
        mIndicatorPadding = typedArray.getDimensionPixelSize(
            R.styleable.HorizontalPageIndicator_indicatorPadding,
            defaultIndicatorPadding
        )
        mStrokeWidth =
            typedArray.getDimensionPixelSize(
                R.styleable.HorizontalPageIndicator_strokeWidth,
                defaultStrokeWidth
            )

        val activePaintColor = typedArray.getColor(
            R.styleable.HorizontalPageIndicator_selectedColor,
            defaultIndicatorActiveColor
        )

        val inactivePaintColor = typedArray.getColor(
            R.styleable.HorizontalPageIndicator_inactiveColor,
            defaultIndicatorInActiveColor
        )
        mActivePaint.color = activePaintColor
        mActivePaint.style = Paint.Style.FILL

        mInactivePaint.color = inactivePaintColor
        mInactivePaint.style = Paint.Style.STROKE
        mInactivePaint.strokeWidth = mStrokeWidth.toFloat()

        if (visibleIndicatorCount < 0) {
            visibleIndicatorCount = mIndicatorSize
        }
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        for (i in 0 until mPageCount) {
            drawCircleAtPosition(
                canvas,
                i * (mIndicatorSize + mIndicatorPadding + (mStrokeWidth / 2)) + (mStrokeWidth / 2),
                mStrokeWidth / 2,
                i == selectedIndex
            )
        }
    }

    private fun drawCircleAtPosition(
        canvas: Canvas,
        left: Int,
        top: Int,
        selected: Boolean
    ) {
        val cx = left + (mIndicatorSize / 2)
        val cy = top + (mIndicatorSize / 2)
        if (selected) {
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mIndicatorRadius.toFloat(), mActivePaint)
        } else {
            canvas.drawCircle(
                cx.toFloat(),
                cy.toFloat(),
                mIndicatorRadius.toFloat(),
                mInactivePaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST &&
            MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST
        ) {
            val newWidth =
                (mPageCount * (mIndicatorSize + mStrokeWidth)) + ((mPageCount - 1) * mIndicatorPadding)
            val newHeight = mIndicatorSize + mStrokeWidth
            setMeasuredDimension(
                max(newWidth, suggestedMinimumWidth),
                max(newHeight, suggestedMinimumHeight)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setupWithViewPager(viewPager: ViewPager) {
        viewPager.adapter?.let {
            mPageCount = it.count
            invalidate()
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                selectedIndex = position
                invalidate()
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        viewPager.addOnAdapterChangeListener { viewPager, oldAdapter, newAdapter ->
            mPageCount = newAdapter?.count ?: 0
            invalidate()
        }
    }
}