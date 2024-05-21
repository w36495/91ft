package com.w36495.senty.view.screen.friend


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.Friend
import com.w36495.senty.viewModel.FriendViewModel

@Composable
fun FriendDialogScreen(
    vm: FriendViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onClickFriend: (Friend) -> Unit,
) {
    val friends by vm.friends.collectAsState()

    FriendDialogContents(
        friends = friends,
        onDismiss = { onDismiss() },
        onClickFriend = {
            onClickFriend(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendDialogContents(
    modifier: Modifier = Modifier,
    friends: List<Friend>,
    onDismiss: () -> Unit,
    onClickFriend: (Friend) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = "친구목록") },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                friends.forEachIndexed { index, friend ->
                    ListItem(
                        headlineContent = { Text(text = friend.name) },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                onClickFriend(friend)
                            }
                    )

                    if (index != friends.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}