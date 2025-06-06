package se.ox.assigment.tink.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import se.ox.assigment.sdk.PaginatedDataSource
import se.ox.assigment.sdk.SdkManager
import se.ox.assigment.sdk.Character

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
                    _error.value = exception.message ?: "Unknown error occurred"
                }
            )

            _isLoading.value = false
        }
    }

    fun loadMoreCharacters() {
        viewModelScope.launch {
            if (!repository.hasMorePages() || _isLoading.value) return@launch

            viewModelScope.launch {
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
    }

    fun retry() {
        viewModelScope.launch {
            repository.reset()
            currentPage = 1
            loadCharacters()
        }
    }

}