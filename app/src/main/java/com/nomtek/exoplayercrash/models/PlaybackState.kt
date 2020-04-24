package com.nomtek.exoplayercrash.models

data class PlaybackState(
    val isPlaying: Boolean,
    val isPrepared: Boolean,
    val isLoading: Boolean,
    val isPauseEnabled: Boolean,
    val isSkipToNextEnabled: Boolean,
    val isSkipToPreviousEnabled: Boolean,
    val isPlayEnabled: Boolean
)
