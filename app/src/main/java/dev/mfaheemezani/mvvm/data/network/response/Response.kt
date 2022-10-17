package dev.mfaheemezani.mvvm.data.network.response

data class Response(
    val status: String?,
    val copyright: String?,
    val section: String?,
    val last_updated: String?,
    val num_results: Int?,
    val results: List<Result>?
)
