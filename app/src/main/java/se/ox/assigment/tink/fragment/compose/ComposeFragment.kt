package se.ox.assigment.tink.fragment.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.ox.assigment.tink.vm.CharacterViewModel
import se.ox.assigment.tinkassigment.ui.theme.RickAndMortyTheme

class ComposeFragment : Fragment() {

    private val viewModel: CharacterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RickAndMortyTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CharacterGrid()
                    }
                }
            }
        }
    }

    @Composable
    private fun CharacterGrid() {
        val characters by viewModel.characters.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            itemsIndexed(characters) { index, character ->
                CharacterCard(
                    character = character,
                    modifier = Modifier.padding(4.dp)
                )

                // Trigger pagination when reaching the end
                if (index == characters.size - 1
//                    && hasMorePages
                    && !isLoading) {
                    LaunchedEffect(Unit) {
                        viewModel.loadMoreCharacters()
                    }
                }
            }

            // Show loading indicator at the bottom
            if (isLoading) {
                item {
                    LoadingIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}