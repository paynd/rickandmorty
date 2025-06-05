package se.ox.assigment.sdk

interface PaginatedDataSource<T> {
    suspend fun loadPage(page: Int): Result<PagedResponse<T>>
    fun getCurrentData(): List<T>
    fun hasMorePages(): Boolean
    fun reset()
}

data class PagedResponse<T>(
    val data: List<T>,
    val currentPage: Int,
    val hasNext: Boolean
)

data class Character(
    val id: Int,
    val name: String,
    val image: String
)

interface CharacterRepository : PaginatedDataSource<Character>