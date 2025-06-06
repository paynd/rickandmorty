package se.ox.assigment.tink.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import se.ox.assigment.tink.R
import se.ox.assigment.tink.adapter.CharacterAdapter
import se.ox.assigment.tink.vm.CharacterViewModel

const val NUMBER_OF_SPANS = 2

class RecyclerFragment : Fragment() {
    private val viewModel: CharacterViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)

        gridLayoutManager = GridLayoutManager(requireContext(), NUMBER_OF_SPANS).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (characterAdapter.getItemViewType(position)) {
                        CharacterAdapter.VIEW_TYPE_LOADING -> 2 // Full width for loading
                        else -> 1 // Half width for character cards
                    }
                }
            }
        }

        recyclerView.layoutManager = gridLayoutManager

        characterAdapter = CharacterAdapter { character ->
            // todo: we can add  click listener here to navigate to character page if needed
        }

        recyclerView.adapter = characterAdapter

        // Added for pagination
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!viewModel.isLoading.value &&
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 6) {
                    viewModel.loadMoreCharacters()
                }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.characters.collect { characters ->
                characterAdapter.submitCharacters(characters)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                characterAdapter.setLoading(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                // todo: here we can show error somehow (toast, dialog or else, like logcat message)
                error?.let {
                    Log.e("###", "Problem happens: $it")
                }
            }
        }
    }

}