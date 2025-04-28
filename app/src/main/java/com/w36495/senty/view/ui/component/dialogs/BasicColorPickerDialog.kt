package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.util.darken
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray40
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyYellow60

@Composable
fun BasicColorPickerDialog(
    onDismiss: () -> Unit,
    onSelectColor: (Color) -> Unit,
) {
    val basicColors = listOf(
        Color(0xFFFFA94D),
        Color(0xFFFFD43B),
        Color(0xFFA9E34B),
        Color(0xFF69DB7C),
        Color(0xFF38D9A9),
        Color(0xFF3BC9DB),
        Color(0xFF4DABF7),
        Color(0xFF748FFC),
        Color(0xFF9775FA),
        Color(0xFFDA77F2),
        Color(0xFFF783AC),
        Color(0xFFFF8787),
        Color(0xFFCED4DA),
        Color(0xFF868E96),
        Color(0xFF343A40),
        Color(0xFF212529),
    )

    var selectColor by remember { mutableStateOf(basicColors[12]) }

    ColorPickerContents(
        basicColors = basicColors.toList(),
        selectColor = selectColor,
        onChangeSelectColor = { selectColor = it },
        onDismiss = { onDismiss() },
        onSelectColor = { onSelectColor(selectColor) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPickerContents(
    basicColors: List<Color>,
    selectColor: Color,
    onChangeSelectColor: (Color) -> Unit,
    onDismiss: () -> Unit,
    onSelectColor: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "색상 선택",
                            style = SentyTheme.typography.headlineSmall,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    }
                )

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    for (i in 0..basicColors.lastIndex step 4) {
                        ColorPaletteRow(
                            basicColors = basicColors,
                            startIndex = i,
                            onClick = onChangeSelectColor,
                            selectColor = selectColor
                        )

                        if (i + 4 <= basicColors.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    SentyFilledButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "선택",
                        onClick = onSelectColor
                    )
                }
            }

        }
    }
}

@Composable
private fun ColorPaletteRow(
    basicColors: List<Color>,
    selectColor: Color,
    startIndex: Int,
    onClick: (Color) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,) {
        if (startIndex + 3 <= basicColors.lastIndex) {
            (startIndex..(startIndex+3)).forEach { index ->
                Box(modifier = Modifier
                    .size(56.dp)
                    .background(basicColors[index], RoundedCornerShape(10.dp))
                    .border(
                        if (selectColor == basicColors[index]) 2.dp else 0.dp,
                        color = selectColor.darken(),
                        RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onClick(basicColors[index]) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectColor == basicColors[index]) {
                        IconButton(onClick = {  }, enabled = false,
                            colors = IconButtonDefaults.iconButtonColors(
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SentyBlack,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicColorPickerDialogPreview() {
    SentyTheme {
        BasicColorPickerDialog(
            onDismiss = {},
            onSelectColor = {},
        )
    }
}