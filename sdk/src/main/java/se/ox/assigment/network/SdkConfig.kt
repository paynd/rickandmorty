package se.ox.assigment.network

data class SdkConfig(
    val baseUrl: String = "https://rickandmortyapi.com/api/",
    val timeoutMs: Long = 30000,
    val maxCacheSize: Int = 1000
)