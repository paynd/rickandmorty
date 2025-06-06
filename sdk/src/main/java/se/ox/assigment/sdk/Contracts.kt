package se.ox.assigment.sdk

interface PaginatedDataSource {
    suspend fun loadPage(page: Int): Result<PagedResponse>
    suspend fun getCurrentData(): List<Character>
    suspend fun hasMorePages(): Boolean
    suspend fun reset()
}

data class PagedResponse(
    val data: List<Character>,
    val currentPage: Int,
    val hasNext: Boolean
) {
    companion object {
        val NONE = PagedResponse(
            data = emptyList(),
            currentPage = 1,
            hasNext = false
        )
    }
}

data class Character(
    val id: Int,
    val name: String,
    val image: String
)
