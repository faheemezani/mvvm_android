package dev.mfaheemezani.mvvm.ui.main

import dev.mfaheemezani.mvvm.data.network.response.*

data class MainScreenState(
    val results: List<Result>,
    val isLoading: Boolean
    )
