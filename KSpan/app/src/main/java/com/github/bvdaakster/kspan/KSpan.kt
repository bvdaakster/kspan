package com.github.bvdaakster.kspan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
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

    // TODO: MaskFilterSpan, MetricAffectingSpan, SuggestionSpan, DynamicDrawableSpan, LocaleSpan,
    //  ReplacementSpan, TextAppearanceSpan, TextLinks.TextLinkSpan

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
     * Apply [StrikethroughSpan] to [indices].
     */
    fun strikethrough(vararg indices: Int) {
        addStyle(indices) {
            StrikethroughSpan()
        }
    }

    /**
     * Apply [UnderlineSpan] to [indices].
     */
    fun underline(vararg indices: Int) {
        addStyle(indices) {
            UnderlineSpan()
        }
    }

    /**
     * Apply [ImageSpan] to [indices] with [bitmap] and [verticalAlignment].
     */
    fun Context.image(vararg indices: Int, bitmap: Bitmap, verticalAlignment: Int = ImageSpan.ALIGN_BASELINE) {
        addStyle(indices) {
            ImageSpan(this, bitmap, verticalAlignment)
        }
    }

    /**
     * Apply [ImageSpan] to [indices] with [resourceId] and [verticalAlignment].
     */
    fun Context.image(vararg indices: Int, @DrawableRes resourceId: Int, verticalAlignment: Int = ImageSpan.ALIGN_BASELINE) {
        addStyle(indices) {
            ImageSpan(this, resourceId, verticalAlignment)
        }
    }

    /**
     * Apply [StyleSpan] to [indices] with [style].
     */
    fun style(vararg indices: Int, style: Int) {
        addStyle(indices) {
            StyleSpan(style)
        }
    }

    /**
     * Apply [SubscriptSpan] to [indices].
     */
    fun subscript(vararg indices: Int) {
        addStyle(indices) {
            SubscriptSpan()
        }
    }

    /**
     * Apply [SuperscriptSpan] to [indices].
     */
    fun superscript(vararg indices: Int) {
        addStyle(indices) {
            SuperscriptSpan()
        }
    }

    /**
     * Apply [TypefaceSpan] to [indices] with [typeface].
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun typeface(vararg indices: Int, typeface: Typeface) {
        addStyle(indices) {
            TypefaceSpan(typeface)
        }
    }

    /**
     * Apply [URLSpan] to [indices] with [url].
     */
    fun url(vararg indices: Int, url: String) {
        addStyle(indices) {
            URLSpan(url)
        }
    }

    /**
     * Apply [ScaleXSpan] to [indices] with [proportion].
     */
    fun scaleX(vararg indices: Int, proportion: Float) {
        addStyle(indices) {
            ScaleXSpan(proportion)
        }
    }

    /**
     * Apply [AbsoluteSizeSpan] to [indices] with [size].
     */
    fun absoluteSize(vararg indices: Int, size: Int) {
        addStyle(indices) {
            AbsoluteSizeSpan(size)
        }
    }

    /**
     * Apply [RelativeSizeSpan] to [indices] with [proportion].
     */
    fun relativeSize(vararg indices: Int, proportion: Float) {
        addStyle(indices) {
            RelativeSizeSpan(proportion)
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