package com.w36495.senty.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.ui.FriendGroupViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendGroupScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onGroupSelected: (FriendGroup) -> Unit,
    onEditClick: () -> Unit,
) {
    val friendGroups by vm.friendGroups.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "친구그룹") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                friendGroups.forEachIndexed { index, group ->
                    FriendGroupItem(
                        group = group,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onGroupSelected(it)
                        }
                    )

                    if (index < friendGroups.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            SentyFilledButton(
                text = "그룹 편집",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                onClick = {
                    onEditClick()
                }
            )
        }
    }
}

@Composable
private fun FriendGroupItem(
    modifier: Modifier = Modifier,
    group: FriendGroup,
    onClick: (FriendGroup) -> Unit
) {
    Row(
        modifier = modifier.clickable { onClick(group) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = group.name)

        IconButton(
            onClick = { },
            modifier = Modifier.background(
                color = Color(group.getIntTypeColor()),
                RoundedCornerShape(10.dp)
            )
        ) { }
    }
}