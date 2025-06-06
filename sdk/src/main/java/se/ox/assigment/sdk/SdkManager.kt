package se.ox.assigment.sdk

import se.ox.assigment.sdk.api.NetworkModule

object SdkManager {
    private var mockRepository: PaginatedDataSource? = null

    fun createCharacterRepository(): PaginatedDataSource {
        return mockRepository ?: CharacterRepository(
            apiService = NetworkModule.createApiService(SdkConfig())
        )
    }

    // For testing
    fun setMockRepository(mock: PaginatedDataSource) {
        mockRepository = mock
    }
}