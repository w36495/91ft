package com.w36495.senty.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun LoadingCircleIndicator(
    modifier: Modifier = Modifier,
    hasBackGround: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (hasBackGround) SentyBlack.copy(alpha = 0.2f) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .dropShadow(
                    shape = RoundedCornerShape(16.dp),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blur = 8.dp
                )
                .background(SentyWhite, RoundedCornerShape(16.dp))
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingCircleIndicatorPreview() {
    SentyTheme {
        LoadingCircleIndicator(
            hasBackGround = false,
        )
    }
}