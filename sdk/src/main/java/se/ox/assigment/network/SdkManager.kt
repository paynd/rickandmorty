package se.ox.assigment.network

import se.ox.assigment.network.api.NetworkModule

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