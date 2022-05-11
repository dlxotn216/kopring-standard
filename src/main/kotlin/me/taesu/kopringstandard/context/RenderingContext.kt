package me.taesu.kopringstandard.context

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */

data class RenderingContext(
    val fontSize: Int,
    val color: Color,
    val fontStyles: List<FontStyle>
)

sealed class Color(val hexCode: String) {
    object White: Color("#FFFFFF")
    object Black: Color("#000000")
}

enum class FontStyle {
    NORMAL, ITALIC, BOLD, UNDERLINE
}