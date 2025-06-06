package se.ox.assigment.sdk


import se.ox.assigment.sdk.api.ApiCharacter

object CharacterMapper {

    private fun mapToDomain(apiCharacter: ApiCharacter): Character {
        return Character(
            id = apiCharacter.id,
            name = apiCharacter.name,
            image = apiCharacter.image
        )
    }

    fun mapToDomainList(apiCharacters: List<ApiCharacter>): List<Character> {
        return apiCharacters.map { mapToDomain(it) }
    }
}