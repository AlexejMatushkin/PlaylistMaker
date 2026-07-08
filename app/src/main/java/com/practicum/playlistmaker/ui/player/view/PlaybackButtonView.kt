package com.practicum.playlistmaker.ui.player.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.practicum.playlistmaker.R
import androidx.core.content.withStyledAttributes

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null
    private var isPlaying: Boolean = false

    var onPlayButtonClickListener: (() -> Unit)? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaybackButtonView) {

            val playSrc = getDrawable(R.styleable.PlaybackButtonView_playDrawable)
            val pauseSrc = getDrawable(R.styleable.PlaybackButtonView_pauseDrawable)
            @ColorInt val tintColor = getColor(
                R.styleable.PlaybackButtonView_tint,
                ContextCompat.getColor(context, android.R.color.black)
            )

            playDrawable = playSrc?.mutate()?.let {
                DrawableCompat.wrap(it).also { wrapped ->
                    DrawableCompat.setTint(wrapped, tintColor)
                }
            }
            pauseDrawable = pauseSrc?.mutate()?.let {
                DrawableCompat.wrap(it).also { wrapped ->
                    DrawableCompat.setTint(wrapped, tintColor)
                }
            }

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = if (isPlaying) pauseDrawable else playDrawable
        val desiredWidth = drawable?.intrinsicWidth ?: DEFAULT_SIZE
        val desiredHeight = drawable?.intrinsicHeight ?: DEFAULT_SIZE
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = (if (isPlaying) pauseDrawable else playDrawable) ?: return
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
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
            requestLayout()
            invalidate()
        }
    }

    companion object {
        private const val DEFAULT_SIZE = 0
    }
}
