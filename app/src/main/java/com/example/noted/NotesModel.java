package com.example.noted;

public class NotesModel {
    String noteId,noteTitle, noteContent, createdAt, categoryTitle, categoryId;

    public NotesModel(String noteId,String noteTitle, String noteContent,String createdAt ) {
        this.createdAt = createdAt;
        this.noteContent = noteContent;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
    }

    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getNoteTitle() {
        return noteTitle;
    }
    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }
    public String getNoteContent() {
        return noteContent;
    }
    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getCategoryTitle() {
        return categoryTitle;
    }
    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }
    public NotesModel() {
    }
}
