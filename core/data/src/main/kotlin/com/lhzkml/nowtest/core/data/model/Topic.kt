package com.lhzkml.nowtest.core.data.model

import com.lhzkml.nowtest.core.database.model.TopicEntity
import com.lhzkml.nowtest.core.network.model.NetworkTopic

fun NetworkTopic.asEntity() = TopicEntity(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl,
)
