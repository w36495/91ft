package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@Composable
fun ScheduleNotificationDialog(
    onClickAllow: () -> Unit,
    onClickRefuse: () -> Unit,
    onClickNextTime: () -> Unit,
) {
    ScheduleNotificationContents(
        onClickAllow = { onClickAllow() },
        onClickRefuse = { onClickRefuse() },
        onClickNextTime = { onClickNextTime() },
    )
}

@Composable
private fun ScheduleNotificationContents(
    modifier: Modifier = Modifier,
    onClickAllow: () -> Unit,
    onClickRefuse: () -> Unit,
    onClickNextTime: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = { /*TODO*/ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "알림 권한을 허용하시겠습니까?")
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SentyFilledButton(
                    text = "권한 허용",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickAllow
                )
                Spacer(modifier = Modifier.height(4.dp))
                SentyFilledButton(
                    text = "권한 거부",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickRefuse,
                    textColor = Color.Black,
                    buttonColor = Color(0xFFD9D9D9)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "다음에 하기",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onClickNextTime() },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScheduleNotificationPreview() {
    SentyTheme {
        ScheduleNotificationContents(
            onClickAllow = {},
            onClickRefuse = {},
            onClickNextTime = {}
        )
    }
}