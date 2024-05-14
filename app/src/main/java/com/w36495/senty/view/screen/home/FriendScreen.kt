package com.w36495.senty.view.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.util.getTextColorByBackgroundColor
import com.w36495.senty.view.entity.FriendEntity
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButtonWithIcon
import com.w36495.senty.viewModel.FriendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    vm: FriendViewModel = hiltViewModel(),
    onClickGroupSetting: () -> Unit,
    onClickAddFriend: () -> Unit,
) {
    val friendList by vm.friends.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "친구목록") },
                actions = {
                    IconButton(onClick = { onClickGroupSetting() }) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        FriendContents(
            modifier = Modifier.padding(innerPadding),
            friends = friendList,
            onClickFriend = {

            },
            onClickGroupSetting = {

            },
            onClickAddFriend = {
                onClickAddFriend()
            }
        )
    }
}

@Composable
private fun FriendContents(
    modifier: Modifier = Modifier,
    friends: List<FriendEntity>,
    onClickFriend: (FriendEntity) -> Unit,
    onClickGroupSetting: () -> Unit,
    onClickAddFriend: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SentyOutlinedButtonWithIcon(
            modifier = Modifier.fillMaxWidth(),
            text = "친구 등록",
            icon = Icons.Default.Add,
            onClick = { onClickAddFriend() }
        )

        friends.forEachIndexed { index, friendEntity ->
            FriendItemContent(
                friend = friendEntity,
                onClickFriend = onClickFriend
            )

            if (index < friends.lastIndex) {
                Divider()
            }
        }
    }
}

@Composable
fun FriendItemContent(
    modifier: Modifier = Modifier,
    friend: FriendEntity,
    onClickFriend: (FriendEntity) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .clickable { onClickFriend(friend) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FriendGroupChip(group = friend.group!!)
            Text(
                text = friend.displayBirthday(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Text(
            text = friend.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = friend.memo,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "💝 받은 선물",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = friend.receivedGiftCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "\uD83C\uDF81 준 선물",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = friend.sentGiftCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendGroupChip(
    modifier: Modifier = Modifier,
    group: FriendGroup,
) {
    Chip(
        modifier = modifier,
        colors = ChipDefaults.chipColors(
            backgroundColor = Color(group.getIntTypeColor())
        ),
        onClick = {},
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = group.name,
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(group.color.getTextColorByBackgroundColor())
        )
    }
}