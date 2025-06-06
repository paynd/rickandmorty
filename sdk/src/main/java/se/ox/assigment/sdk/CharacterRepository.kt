package se.ox.assigment.sdk

import androidx.collection.LruCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.ox.assigment.sdk.api.NetworkModule

class CharacterRepository : PaginatedDataSource {
    private val apiService = NetworkModule.apiService

    private val repositoryDispatcher: CoroutineDispatcher =
        Dispatchers.IO.limitedParallelism(1)

    // Memory management
    private val maxCacheSize = 500
    // No synchronization needed - single thread guarantees sequential access
    private val characterCache = LruCache<Int, Character>(maxCacheSize)

    private var currentPage = 1
    private var totalPages = 1
    private var hasMore = true

    override suspend fun loadPage(page: Int): Result<PagedResponse> {
        return withContext(repositoryDispatcher) {
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
                            characterCache.evictAll()
                        }

                        characters.forEach { character ->
                            characterCache.put(character.id, character)
                        }

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

    override suspend fun getCurrentData(): List<Character> {
        return withContext(repositoryDispatcher) {
            characterCache.snapshot().values.toList()
        }
    }

    override suspend fun hasMorePages(): Boolean {
        return withContext(repositoryDispatcher) {
            hasMore
        }
    }

    override suspend fun reset() = withContext(repositoryDispatcher) {
        characterCache.evictAll()
        currentPage = 1
        totalPages = 1
        hasMore = true
    }
}