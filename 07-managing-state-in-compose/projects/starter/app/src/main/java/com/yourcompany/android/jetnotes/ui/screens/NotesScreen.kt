package com.yourcompany.android.jetnotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.yourcompany.android.jetnotes.domain.model.NoteModel
import com.yourcompany.android.jetnotes.ui.components.Note
import com.yourcompany.android.jetnotes.ui.components.TopAppBar
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel

@Composable
fun NotesScreen(viewModel: MainViewModel) {

  val notes: List<NoteModel> by viewModel
    .notesNotInTrash
    .observeAsState(listOf())

  Column {
    TopAppBar(
      title = "JetNotes",
      icon = Icons.Filled.List,
      onIconClick = {}
    )
    LazyColumn {
      items(count = notes.size) { noteIndex ->
        val note = notes[noteIndex]
        Note(
          note = note,
          onNoteClick = {
            viewModel.onNoteClick(it)
          },
          onNoteCheckedChange = {
            viewModel.onNoteCheckedChange(it)
          }
        )
      }
    }
  }
}