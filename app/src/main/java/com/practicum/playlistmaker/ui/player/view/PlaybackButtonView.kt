package com.practicum.playlistmaker.ui.player.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.practicum.playlistmaker.R
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.createBitmap

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var isPlaying: Boolean = false

    private val imageRect = RectF()
    private val srcRect = Rect()
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onPlayButtonClickListener: (() -> Unit)? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaybackButtonView) {

            val playDrawable = getDrawable(R.styleable.PlaybackButtonView_playDrawable)
            val pauseDrawable = getDrawable(R.styleable.PlaybackButtonView_pauseDrawable)
            @ColorInt val tintColor = getColor(
                R.styleable.PlaybackButtonView_tint,
                ContextCompat.getColor(context, android.R.color.black)
            )

            playBitmap = playDrawable?.let { drawableToBitmap(it, tintColor) }
            pauseBitmap = pauseDrawable?.let { drawableToBitmap(it, tintColor) }

        }
    }

    private fun drawableToBitmap(drawable: Drawable, @ColorInt tintColor: Int): Bitmap {
        val wrappedDrawable = DrawableCompat.wrap(drawable.mutate())
        DrawableCompat.setTint(wrappedDrawable, tintColor)

        val width = wrappedDrawable.intrinsicWidth
        val height = wrappedDrawable.intrinsicHeight
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        wrappedDrawable.setBounds(0, 0, width, height)
        wrappedDrawable.draw(canvas)
        return bitmap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        val desiredWidth = bitmap?.width ?: DEFAULT_SIZE
        val desiredHeight = bitmap?.height ?: DEFAULT_SIZE
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
        updateSrcRect()
    }

    private fun updateSrcRect() {
        val bitmap = (if (isPlaying) pauseBitmap else playBitmap)
        if (bitmap != null) {
            srcRect.set(0, 0, bitmap.width, bitmap.height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentBitmap = (if (isPlaying) pauseBitmap else playBitmap) ?: return
        canvas.drawBitmap(currentBitmap, srcRect, imageRect, bitmapPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                isPlaying = !isPlaying
                invalidate()
                onPlayButtonClickListener?.invoke()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPlayingState(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            updateSrcRect()
            requestLayout()
            invalidate()
        }
    }

    companion object {
        private const val DEFAULT_SIZE = 0
    }
}
