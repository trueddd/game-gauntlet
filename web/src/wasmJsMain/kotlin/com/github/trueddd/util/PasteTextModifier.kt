package com.github.trueddd.util

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import kotlinx.coroutines.launch

fun Modifier.updateTextFieldOnCtrlV(
    textFieldValue: TextFieldValue,
    onTextFileValueChanged: (TextFieldValue) -> Unit
): Modifier {
    return composed {
        val scope = rememberCoroutineScope()
        onKeyEvent {
            return@onKeyEvent when {
                it.isCtrlPressed && it.key == Key.V && it.type == KeyEventType.KeyUp -> {
                    scope.launch {
                        val copiedText = readFromClipboard()
                        val newValue = textFieldValue.copy(
                            text = buildString {
                                append(textFieldValue.getTextBeforeSelection(textFieldValue.text.length).text)
                                append(copiedText)
                                append(textFieldValue.getTextAfterSelection(textFieldValue.text.length).text)
                            },
                            selection = TextRange(
                                index = textFieldValue.selection.start + copiedText.length,
                            )
                        )
                        onTextFileValueChanged(newValue)
                    }
                    true
                }
                else -> false
            }
        }
    }
}
