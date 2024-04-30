package com.github.trueddd.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.github.trueddd.theme.Colors

@Stable
private fun spanStyleFor(char: Char): SpanStyle {
    return when (char) {
        '+' -> SpanStyle(fontWeight = FontWeight.Bold, color = Colors.GameStatus.Finished)
        '-' -> SpanStyle(fontWeight = FontWeight.Bold, color = Colors.GameStatus.Dropped)
        else -> SpanStyle()
    }
}

@Stable
fun String.applyModifiersDecoration(): AnnotatedString {
    return buildAnnotatedString {
        val source = this@applyModifiersDecoration
        if (!source.contains('`')) {
            append(source)
            return@buildAnnotatedString
        }
        var styleInStack = false
        source.forEachIndexed { index, char ->
            if (char == '`') {
                if (styleInStack) {
                    pop()
                } else {
                    pushStyle(spanStyleFor(source[index + 1]))
                }
                styleInStack = !styleInStack
            } else {
                append(char)
            }
        }
    }
}
