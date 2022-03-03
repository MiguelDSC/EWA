package app.repositories.note;
import app.models.note.Note;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class NoteRepository implements NoteInterface {

    @PersistenceContext
    public EntityManager em;

    @Override
    public Note findById(int id) {
        return em.find(Note.class, id);
    }

    //get all notes
    @Override
    public List<Note> findAll(String[] category) {
        return em.createQuery("SELECT n FROM Note n", Note.class).getResultList();
    }

    //create query from string
    public TypedQuery<Note> createQuery(String queryString) {
        return this.em.createQuery(queryString, Note.class);
    }

    //used to concat query, if active filter contains any category
    public String whereOrAndWhere(boolean containsCategory, String query) {
        if (!containsCategory){
            return  " AND ( "  + query + " ";
        }

        return " OR "  + query + " ";
    }

    //save note that gets recieved from front-end
    @Override
    public Note save(Note note) {
        //if we're updating a note, set new date to updatedDate
        if (this.findById(note.getId()) == null) {
            note.setCreatedDate(new Date());
        }else if(this.findById(note.getId()) != null){
            note.setUpdatedDate(new Date());
        }

        //save or update note, and return persisted note
        return em.merge(note);
    }

    //delete note with incoming note id and return deleted note
    @Override
    public Note deleteById(int id) {
        Note toDelete = this.findById(id);
        em.remove(toDelete);
        return toDelete;
    }
}
