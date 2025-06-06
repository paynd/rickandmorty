package se.ox.assigment.tink.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import se.ox.assigment.network.PaginatedDataSource
import se.ox.assigment.network.SdkManager
import se.ox.assigment.network.Character
import se.ox.assigment.network.errors.CharacterError

class CharacterViewModel : ViewModel() {
    private val repository: PaginatedDataSource = SdkManager.createCharacterRepository()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // todo: take a look into channel realisation from last project to avoid showing duplication
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 1

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.loadPage(currentPage).fold(
                onSuccess = { response ->
                    _characters.value = repository.getCurrentData()
                    currentPage++
                },
                onFailure = { exception ->
                    // here can be different scenarios of error handling,
                    // but we will stick to simple error message
                    when (exception) {
                        is CharacterError.NetworkError -> {
                            _error.value = exception.message
                        }

                        is CharacterError.ApiError -> {
                            _error.value = exception.message
                        }

                        is CharacterError.HttpError -> {
                            _error.value = exception.message
                        }

                        is CharacterError.Unknown -> {
                            _error.value = exception.message
                        }
                    }
                }
            )

            _isLoading.value = false
        }
    }

    fun loadMoreCharacters() {
        if (_isLoading.value) return

        viewModelScope.launch {
            if (!repository.hasMorePages()) return@launch
            _isLoading.value = true

            repository.loadPage(currentPage).fold(
                onSuccess = {
                    _characters.value = repository.getCurrentData()
                    currentPage++
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load more characters"
                }
            )

            _isLoading.value = false
        }
    }

    // todo
    fun retry() {
        viewModelScope.launch {
            repository.reset()
            currentPage = 1
            loadCharacters()
        }
    }

}