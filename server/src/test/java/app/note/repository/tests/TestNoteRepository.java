package app.note.repository.tests;

import app.models.note.Note;
import app.models.note.NoteCategory;
import app.repositories.note.NoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.persistence.TypedQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestNoteRepository {

    @Autowired
    @Qualifier("noteRepository")
    public NoteRepository noteRepository;


    /**
     * Test if you can find a note.
     */
    @Test
    public void testFindingANote() {

        //arrange, make a new note
        Note note = new Note(null, NoteCategory.BOTANY, "Test title 1", "Test content 1", true);

        //act save the note to the database
        note = noteRepository.save(note);

        //assert & crosscheck
        assertThat(noteRepository.findById(note.getId()).getTitle()).isNotEqualTo("wrong title");
        assertThat(noteRepository.findById(note.getId()).getTitle()).isEqualTo(note.getTitle());

    }

    /**
     * Test if you can delete a note.
     */
    @Test
    public void testDeletingANote() {
        //arrange
        Note note = new Note(null, NoteCategory.BOTANY, "Water calculations", "aasfjjn adsaskdlj", true);
        note = noteRepository.save(note);

        //check if note exists in database before deleting
        assertThat(noteRepository.findById(note.getId())).isNotNull();

        //act delete note
        Note deletedNote = this.noteRepository.deleteById(note.getId());

        //assert that note is deleted
        assertNull(this.noteRepository.findById(deletedNote.getId()));

    }

    /**
     * Test if you can update a note.
     */
    @Test
    public void testUpdatingANote() {

        //arrange, make a new note
        Note note = new Note(null, NoteCategory.BOTANY, "Water calculations", "aasfjjn adsaskdlj", true);
        note = noteRepository.save(note);

        //check if note exists in database before updating
        assertThat(noteRepository.findById(note.getId())).isNotNull();

        //check before act that this note shared property is set to true
        assertTrue(note.isShared());

        //act, set shared property of this note to false
        note.setShared(false);
        this.noteRepository.save(note);
        note = this.noteRepository.findById(note.getId());

        //assert that note shared property has succesfully been updated to false
        assertFalse(note.isShared());

    }

    /**
     * Test if we get a typed query.
     */
    @Test
    public void testGetTypedQuery() {
        //arrange, string query
        String query = "SELECT n FROM Note n WHERE n.shared = true";

        //act, create a typed query
        TypedQuery<Note> newQuery = this.noteRepository.createQuery(query);

        //assert typed query is not null
        assertNotNull(newQuery);
    }


}
