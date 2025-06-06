package se.ox.assigment.tink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import se.ox.assigment.tink.R
import se.ox.assigment.sdk.Character

class CharacterAdapter(
    private val onCharacterClick: (Character) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_CHARACTER = 0
        const val VIEW_TYPE_LOADING = 1
    }

    private val characters = mutableListOf<Character>()
    private var isLoading = false

    fun submitCharacters(newCharacters: List<Character>) {
        val oldSize = characters.size
        characters.clear()
        characters.addAll(newCharacters)

        // Notify about the changes
        if (oldSize == 0) {
            notifyDataSetChanged() // should be fine since it's initial dataset loading
        } else {
            if (newCharacters.size > oldSize) {
                notifyItemRangeInserted(oldSize, newCharacters.size - oldSize)
            }
        }
    }

    fun setLoading(loading: Boolean) {
        val wasLoading = isLoading
        isLoading = loading

        if (wasLoading != loading) {
            if (loading) {
                notifyItemInserted(characters.size)
            } else {
                // to prevent
                // IndexOutOfBoundsException in RecyclerView triggered by RecyclerView.onLayout()
                // see https://issuetracker.google.com/issues/37007605#hc141
                notifyItemRemoved(characters.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return characters.size + if (isLoading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < characters.size) {
            VIEW_TYPE_CHARACTER
        } else {
            VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CHARACTER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_character, parent, false)
                CharacterViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CharacterViewHolder -> {
                val character = characters[position]
                holder.bind(character, onCharacterClick)
            }
            is LoadingViewHolder -> {
                // Loading view doesn't need binding
            }
        }
    }

    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val characterImage: ImageView = itemView.findViewById(R.id.character_image)
        private val characterName: TextView = itemView.findViewById(R.id.character_name)
        private val imageProgressBar: ProgressBar = itemView.findViewById(R.id.image_progress_bar)

        fun bind(character: Character, onCharacterClick: (Character) -> Unit) {
            characterName.text = character.name

            // Show progress bar initially
            imageProgressBar.visibility = View.VISIBLE

            // Load image with Coil
            characterImage.load(character.image) {
                crossfade(true)
                listener(
                    onStart = {
                        imageProgressBar.visibility = View.VISIBLE
                    },
                    onSuccess = { _, _ ->
                        imageProgressBar.visibility = View.GONE
                    },
                    onError = { _, _ ->
                        imageProgressBar.visibility = View.GONE
                        characterImage.setImageResource(R.drawable.ic_placeholder)
                    }
                )
            }

            itemView.setOnClickListener {
                onCharacterClick(character)
            }
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}