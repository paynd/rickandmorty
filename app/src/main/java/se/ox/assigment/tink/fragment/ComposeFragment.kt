package se.ox.assigment.tink.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import se.ox.assigment.tinkassigment.ui.theme.RickAndMortyTheme

class ComposeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RickAndMortyTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Yellow),
                    ) {
                        Text(text = "Compose")
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load initial data if empty
//        if (viewModel.characters.value.isEmpty()) {
//            viewModel.loadInitialData()
//        }
    }
}