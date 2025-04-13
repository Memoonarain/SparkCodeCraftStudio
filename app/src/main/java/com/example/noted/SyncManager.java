package com.example.noted;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncManager {

    private FirebaseFirestore firestore;
    private NotesDatabaseHelper NotesDatabaseHelper;
    private String userId;

    public SyncManager(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.NotesDatabaseHelper = new NotesDatabaseHelper(context); // your custom class
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void syncNotes() {
        CollectionReference notesRef = firestore
                .collection("users")
                .document(userId)
                .collection("notes");

        notesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<NotesModel> localNotes = NotesDatabaseHelper.fetchAllNotes(); // Your local SQLite method

            Map<String, NotesModel> localNotesMap = new HashMap<>();
            for (NotesModel note : localNotes) {
                localNotesMap.put(note.getNoteId(), note);
            }

            // 1. Compare Firestore notes with local
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                NotesModel cloudNote = doc.toObject(NotesModel.class);
                NotesModel localNote = localNotesMap.get(cloudNote.getNoteId());

                if (localNote == null) {
                    // 🔻 Cloud note not in local → add to SQLite
                    String noteId = cloudNote.getNoteId();
                    String title = cloudNote.getNoteTitle();
                    String noteContent = cloudNote.getNoteContent();
                    long timestamp = cloudNote.getTimestamp();
                    NotesDatabaseHelper.insertNote(noteId,title,noteContent,timestamp);
                } else {
                    // 🔄 Compare timestamps
                    String  noteId = cloudNote.getNoteId();
                    String title = cloudNote.getNoteTitle();
                    String noteContent = cloudNote.getNoteContent();
                    long timestamp = cloudNote.getTimestamp();
                    if (cloudNote.getTimestamp() > localNote.getTimestamp()) {
                        NotesDatabaseHelper.updateNote(noteId,title,noteContent,timestamp); // Cloud is newer
                    } else if (cloudNote.getTimestamp() < localNote.getTimestamp()) {
                        // Local is newer → upload to Firestore
                        notesRef.document(localNote.getNoteId()).set(localNote);
                    }
                }

                // Remove from local map to track unmatched notes
                localNotesMap.remove(cloudNote.getNoteId());
            }

            // 2. Local notes not in Firestore → upload
            for (NotesModel unsyncedNote : localNotesMap.values()) {
                notesRef.document(unsyncedNote.getNoteId()).set(unsyncedNote);
            }

            Log.d("Sync", "Note sync complete.");
        }).addOnFailureListener(e -> {
            Log.e("Sync", "Failed to sync notes", e);
        });
    }
}
