package com.pkndegwa.mycarmaintenance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pkndegwa.mycarmaintenance.databinding.NoteListItemBinding
import com.pkndegwa.mycarmaintenance.models.Note

class NoteListAdapter(private val onItemClicked: (Note) -> Unit) :
    ListAdapter<Note, NoteListAdapter.NoteViewHolder>(DiffCallback) {

    class NoteViewHolder(private val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.apply {
                noteTitleTextView.text = note.noteTitle
                noteBodyTextView.text = note.noteBody
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position)
        holder.bind(currentNote)
        holder.itemView.setOnClickListener { onItemClicked(currentNote) }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }
    }
}