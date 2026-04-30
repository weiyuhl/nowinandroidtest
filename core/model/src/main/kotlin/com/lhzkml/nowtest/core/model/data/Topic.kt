package com.lhzkml.nowtest.core.model.data

/**
 * External data layer representation of a Nt Topic
 */
data class Topic(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val url: String,
    val imageUrl: String,
)
