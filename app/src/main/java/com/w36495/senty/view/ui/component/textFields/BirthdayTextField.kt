package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun BirthdayTextField(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    onCompleted: (String) -> Unit
) {

    BasicTextField(
        value = text,
        onValueChange = {
            if (text.length <= 2) {
                onValueChange(it)
            }
        },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = { onCompleted(text) }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(2) { index ->
                val currentNum = text.getOrNull(index)

                BirthdayTextFieldContainer(
                    day = currentNum?.digitToInt()?.toString() ?: " ",
                    isFocused = index == text.lastIndex
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(text = title,
                modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun BirthdayTextFieldContainer(
    day: String,
    isFocused: Boolean,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
            .border(
                if (isFocused) BorderStroke(1.dp, SentyGreen60) else BorderStroke(
                    0.dp,
                    Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = day)
    }
}