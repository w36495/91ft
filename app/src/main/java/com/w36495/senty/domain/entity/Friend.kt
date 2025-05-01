package com.w36495.senty.domain.entity

data class Friend(
    val id: String,
    val name: String,
    val birthday: String,
    val groupId: String,
    val groupName: String,
    val groupColor: String,
    val memo: String,
    val received: Int,
    val sent: Int,
    val createdAt: Long,
    val updatedAt: Long,
)