package se.ox.assigment.sdk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.ox.assigment.sdk.api.NetworkModule

class CharacterRepository : PaginatedDataSource {
    private val apiService = NetworkModule.apiService
    private val allCharacters = mutableListOf<Character>()
    private var currentPage = 1
    private var totalPages = 1
    private var hasMore = true

    override suspend fun loadPage(page: Int): Result<PagedResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCharacters(page)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val characters = apiResponse.results.map { apiCharacter ->
                            Character(
                                id = apiCharacter.id,
                                name = apiCharacter.name,
                                image = apiCharacter.image
                            )
                        }

                        if (page == 1) {
                            allCharacters.clear()
                        }
                        allCharacters.addAll(characters)

                        currentPage = page
                        totalPages = apiResponse.info.pages
                        hasMore = apiResponse.info.next != null

                        val pagedResponse = PagedResponse(
                            data = characters,
                            currentPage = page,
                            hasNext = hasMore
                        )

                        Result.success(pagedResponse)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getCurrentData(): List<Character> {
        return allCharacters.toList()
    }

    override fun hasMorePages(): Boolean {
        return hasMore
    }

    override fun reset() {
        allCharacters.clear()
        currentPage = 1
        totalPages = 1
        hasMore = true
    }
}