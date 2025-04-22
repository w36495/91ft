package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendSelectionDialog(
    vm: FriendSelectionViewModel = hiltViewModel(),
    onClickFriend: (FriendUiModel) -> Unit,
    onClickFriendAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.refresh()
    }

    val friends by vm.friends.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.friend_selection_title),
                            style = SentyTheme.typography.headlineSmall,
                        )
                    },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                when {
                    friends.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color(0xFFFBFBFB))
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.friend_selection_empty_text),
                                    style = SentyTheme.typography.bodyMedium
                                        .copy(color = SentyGray70),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                )
                            }

                            SentyFilledButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                text = stringResource(id = R.string.friend_selection_empty_button_text),
                                onClick = onClickFriendAdd
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .padding(bottom = 16.dp),
                        ) {
                            items(friends.size) { index ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onClickFriend(friends[index]) },
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = friends[index].name,
                                        style = SentyTheme.typography.bodyMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(vertical = 16.dp, horizontal = 12.dp),
                                    )

                                    if (index != friends.lastIndex) {
                                        HorizontalDivider(
                                            color = SentyGray20,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                        )
                                    }
                                }
                            }
                        }

                        SentyFilledButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            text = stringResource(id = R.string.friend_selection_empty_button_text),
                            onClick = onClickFriendAdd
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 500)
@Composable
private fun FriendSelectionDialogPreview() {
    SentyTheme {
        FriendSelectionDialog(
            onDismiss = {},
            onClickFriend = {},
            onClickFriendAdd = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun EmptyFriendSelectionDialogPreview() {
    SentyTheme {
        FriendSelectionDialog(
            onDismiss = {},
            onClickFriend = {},
            onClickFriendAdd = {},
        )
    }
}