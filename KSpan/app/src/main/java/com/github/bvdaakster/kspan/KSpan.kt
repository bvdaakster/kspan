package com.github.bvdaakster.kspan

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * KSpan, the Spannable Builder.
 *
 * Creates a [Spannable] by joining the segments of [stringSegments] and applying the styles applied
 *  using this builder.
 *
 * Set [insertSpaces] to `true` to insert spaces when joining the segments.
 */
class KSpan(
    private val stringSegments: Array<CharSequence>,
    private val insertSpaces: Boolean) {

    /**
     * The style list per [stringSegments] index.
     */
    private val styles = Array<MutableList<CharacterStyle>>(stringSegments.size) {
        mutableListOf()
    }

    /**
     * Add a style to the [indices] as created by [styleFactory].
     */
    fun addStyle(indices: IntArray, styleFactory: () -> CharacterStyle) {
        indices.forEach { index ->
            styles[index].add(styleFactory())
        }
    }

    /**
     * Apply [ForegroundColorSpan] to [indices] with the color resource [colorRes].
     */
    fun Context.foregroundColor(@ColorRes colorRes: Int, vararg indices: Int) {
        addStyle(indices) {
            ForegroundColorSpan(ContextCompat.getColor(this, colorRes))
        }
    }

    /**
     * Apply [BackgroundColorSpan] to [indices] with the color resource [colorRes].
     */
    fun Context.backgroundColor(@ColorRes colorRes: Int, vararg indices: Int) {
        addStyle(indices) {
            BackgroundColorSpan(ContextCompat.getColor(this, colorRes))
        }
    }

    /**
     * Apply [ClickableSpan] to [indices] with the listener [onClick].
     *
     * Calls [TextView.setMovementMethod] setting the movement method to
     *  [LinkMovementMethod.getInstance].
     */
    fun TextView.click(vararg indices: Int, onClick: () -> Unit) {
        movementMethod = LinkMovementMethod.getInstance()
        addStyle(indices) {
            object : ClickableSpan() {
                override fun onClick(p0: View) = onClick()
            }
        }
    }

    /**
     * Calculates the start index of segment [index] within the final string.
     */
    private fun calculateIndexStart(index: Int): Int {
        val extra = if(insertSpaces) {
            1
        }
        else {
            0
        }
        return stringSegments.take(index).fold(0) { acc: Int, string: CharSequence ->
            acc + string.length + extra
        }
    }

    /**
     * Calculates the end index of segment [index] within the final string.
     */
    private fun calculateIndexEnd(index: Int) = calculateIndexStart(index + 1)

    /**
     * Builds the [Spannable].
     */
    fun build(): Spannable {
        val separator = if(insertSpaces) {
            " "
        }
        else{
            ""
        }
        val spanBuilder = SpannableStringBuilder(stringSegments.joinToString(separator))

        for(i in stringSegments.indices) {
            val start = calculateIndexStart(i)
            val end = calculateIndexEnd(i)

            styles[i].forEach { style ->
                spanBuilder.setSpan(style, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }

        return spanBuilder
    }
}

/**
 * Creates a [Spannable] by joining the segments of the string-array at [stringArrayRes] and
 *  applying the styles applied using [builder].
 *
 * Set [insertSpaces] to `true` to insert spaces when joining the segments.
 */
fun Context.kspan(@ArrayRes stringArrayRes: Int, insertSpaces: Boolean = false, builder: KSpan.() -> Unit): Spannable {
    return KSpan(resources.getTextArray(stringArrayRes), insertSpaces).run {
        builder()
        build()
    }
}