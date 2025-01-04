package com.example.noted;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    List<NotesModel> notesModelList;
    Context context;
    public NotesAdapter(List<NotesModel> notesModelList, Context context){
        this.context = context;
        this.notesModelList = notesModelList;
    }

    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {
        NotesModel notesModel = notesModelList.get(position);
        holder.noteTitle.setText(notesModel.getNoteTitle());
        holder.noteContent.setText(notesModel.getNoteContent());
        holder.noteCreatedDate.setText(notesModel.getCreatedAt());
        holder.optionsImage.setOnClickListener(v -> showOptionsMenu(notesModel.getNoteId()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteEditor.class);
            intent.putExtra("NOTE_ID", notesModel.getNoteId());  // Pass the note ID to the editor
            context.startActivity(intent);
        });
    }

    private void showOptionsMenu(String noteId) {

    }

    @Override
    public int getItemCount() {
        return notesModelList.size();    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, noteCreatedDate;
        ImageView optionsImage;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteContent = itemView.findViewById(R.id.note_content);
            noteCreatedDate = itemView.findViewById(R.id.note_created_date);
            optionsImage = itemView.findViewById(R.id.note_options);
        }
    }
}
