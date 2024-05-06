package com.w36495.senty.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.theme.PretendardKr

@Composable
fun MainScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Senty",
            fontFamily = PretendardKr,
            fontStyle = FontStyle.Normal,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "정성스런 선물을 주고받았을 때의",
            fontFamily = PretendardKr,
            fontStyle = FontStyle.Normal,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "감정과 느낌을 senty와 함께 기록해보세요.",
            fontFamily = PretendardKr,
            fontStyle = FontStyle.Normal,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
        SentyFilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "이메일로 로그인",
            onClick = onLoginClick
        )
        Spacer(modifier = Modifier.height(4.dp))
        SentyOutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "이메일로 회원가입",
            onClick = onSignUpClick
        )
    }
}