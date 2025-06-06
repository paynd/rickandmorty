package se.ox.assigment.sdk

interface PaginatedDataSource {
    suspend fun loadPage(page: Int): Result<PagedResponse>
    fun getCurrentData(): List<Character>
    fun hasMorePages(): Boolean
    fun reset()
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
