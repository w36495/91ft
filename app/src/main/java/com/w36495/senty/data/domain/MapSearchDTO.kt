package com.w36495.senty.data.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MapSearchDTO(
    @JsonNames("addresses")
    val addresses: List<Addresse>,
    @JsonNames("errorMessage")
    val errorMessage: String,
    @JsonNames("meta")
    val meta: Meta,
    @JsonNames("status")
    val status: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Meta(
    @JsonNames("count")
    val count: Int,
    @JsonNames("page")
    val page: Int = 0,
    @JsonNames("totalCount")
    val totalCount: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Addresse(
    @JsonNames("addressElements")
    val addressElements: List<AddressElement>,
    @JsonNames("distance")
    val distance: Double,
    @JsonNames("englishAddress")
    val englishAddress: String,
    @JsonNames("jibunAddress")
    val jibunAddress: String,
    @JsonNames("roadAddress")
    val roadAddress: String,
    @JsonNames("x")
    val x: String,
    @JsonNames("y")
    val y: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AddressElement(
    @JsonNames("code")
    val code: String,
    @JsonNames("longName")
    val longName: String,
    @JsonNames("shortName")
    val shortName: String,
    @JsonNames("types")
    val types: List<String>
)