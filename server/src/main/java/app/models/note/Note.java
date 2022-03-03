package app.models.note;

import app.models.UserTeam;
import app.models.user.User;

import javax.persistence.*;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "NOTE_CATEGORY")
    NoteCategory noteCategory;
    @ManyToOne()
    UserTeam createdBy;
    @Column(name = "TITLE")
    String title;
    @Column(name = "CONTENT", columnDefinition="TEXT")
    String content;
    @Column(name = "SHARED")
    boolean shared;
    @Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public java.util.Date createdDate;
    @Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public java.util.Date updatedDate;

    //empty note constructor for JPA
    public Note() {
    }

    //standard note construtor
    public Note(UserTeam createdBy, NoteCategory noteCategory, String title, String content, boolean shared) {
        this.createdBy = createdBy;
        this.noteCategory = noteCategory;
        this.title = title;
        this.content = content;
        this.shared = shared;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserTeam getCreatedBy() {
        return createdBy;
    }

    public NoteCategory getNoteCategory() {
        return noteCategory;
    }

    public void setNoteCategory(NoteCategory noteCategory) {
        this.noteCategory = noteCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

}



