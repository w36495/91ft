package com.w36495.senty.view.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.textFields.BirthdayTextField
import com.w36495.senty.view.ui.theme.Green40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdaySelectionDialogScreen(
    onClickComplete: (String, String) -> Unit
) {
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = { Text(text = "생일") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 48.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//
//                BirthdayTextField(
//                    title = "월",
//                    text = month,
//                    onValueChange = { month = it },
//                    onCompleted = {}
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//
//                BirthdayTextField(
//                    title = "일",
//                    text = day,
//                    onValueChange = { day = it },
//                    onCompleted = {}
//                )
//
//            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                var monthExpend by remember {
                    mutableStateOf(false)
                }
                var month by remember { mutableStateOf("12") }

                Button(
                    onClick = { monthExpend = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF4F4F4),
                        contentColor = Color.Black
                    )) {
                    Text(text = month,
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontSize = 20.sp)
                }
                DropdownMenu(
                    expanded = monthExpend, onDismissRequest = { monthExpend = false }) {

                    (1..12).forEach {
                        DropdownMenuItem(
                            text = { Text(text = it.toString()) },
                            onClick = { month = it.toString()
                            monthExpend = false}
                        )
                    }
                }

                Text(text = "월",
                    modifier = Modifier.padding(horizontal = 8.dp))
            }


            SentyFilledButton(
                text = "확인",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                onClickComplete(month, day)
            }

            Spacer(modifier = Modifier.height(4.dp))
            SentyOutlinedButton(
                text = "재입력",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                month = ""
                day = ""
            }
        }
    }
}

//@Composable
//fun BirthdaySelectionItem(
//    items: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
//) {
//
//    Column {
//        items.forEach {
//            Text(text = it.toString())
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun BirthdaySelectionDialogScreenPreview() {
    SentyTheme {
        BirthdaySelectionDialogScreen(
            onClickComplete = { _, _ ->

            }
        )
    }
}