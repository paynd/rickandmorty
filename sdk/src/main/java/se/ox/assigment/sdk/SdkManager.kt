package se.ox.assigment.sdk

object SdkManager {
    fun createCharacterRepository(): PaginatedDataSource {
        return CharacterRepository()
    }
}