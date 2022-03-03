package app.repositories.note;

import app.models.note.Note;

import javax.persistence.TypedQuery;
import java.util.List;

public interface NoteInterface {
    List<Note> findAll(String[] category);
    TypedQuery<Note> createQuery(String queryString);
    Note findById(int id);
    Note save(Note note);
    Note deleteById(int id);
}
