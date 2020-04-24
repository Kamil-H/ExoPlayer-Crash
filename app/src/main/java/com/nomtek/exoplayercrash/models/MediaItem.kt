package com.nomtek.exoplayercrash.models

data class MediaItem(
    val id: String,
    val title: String,
    val displayTitle: String,
    val mediaUri: String,
    val advertisement: Advertisement?
)

data class Advertisement(
    val id: String,
    val reportingData: List<ReportingData>
)

data class ReportingData(
    val text: String,
    val position: Double
)
