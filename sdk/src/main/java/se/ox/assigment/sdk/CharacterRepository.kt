package se.ox.assigment.sdk

import androidx.collection.LruCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.ox.assigment.sdk.api.ApiService
import se.ox.assigment.sdk.errors.CharacterError

class CharacterRepository(
    config: SdkConfig = SdkConfig(),
    private val apiService: ApiService,
) : PaginatedDataSource {
    private val mapper = CharacterMapper

    private val repositoryDispatcher: CoroutineDispatcher =
        Dispatchers.IO.limitedParallelism(1)


    // No synchronization needed - single thread guarantees sequential access
    private val characterCache = LruCache<Int, Character>(config.maxCacheSize)

    private var currentPage = 1
    private var totalPages = 1
    private var hasMore = true

    override suspend fun loadPage(page: Int): Result<PagedResponse> {
        return withContext(repositoryDispatcher) {
            try {
                val response = apiService.getCharacters(page)

                if (response.isSuccessful) {
                    val apiResponse = response.body() ?: return@withContext Result.failure(
                        CharacterError.ApiError
                    )

                    val characters = mapper.mapToDomainList(apiResponse.results)

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
                    Result.failure(CharacterError.HttpError(response.code()))
                }
            } catch (e: Exception) {
                val error = when (e) {
                    is java.net.UnknownHostException,
                    is java.net.SocketTimeoutException -> CharacterError.NetworkError

                    else -> CharacterError.Unknown(e)
                }
                Result.failure(error)
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