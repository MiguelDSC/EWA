package app.rest.note;

import app.models.UserTeam;
import app.models.note.Note;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.note.NoteRepository;
import app.repositories.team.TeamRepositoryJPA;
import app.repositories.user.UserRepository;
import app.repositories.userteam.UserTeamRepository;
import app.rest.security.JWTokenInfo;
import app.rest.security.JWTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NoteController {


    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    @Autowired
    private TeamRepositoryJPA teamRepositoryJPA;

    @Autowired
    @Qualifier("userTeamRepositoryJPA")
    private UserTeamRepository userTeamRepositoryJPA;

    @Autowired
    private JWTokenUtil jwTokenUtil;


    //get all notes, with optional active filters
    @GetMapping("/note-page")
    public List<Note> getAllNotes(@RequestHeader("authorization") String authorizationHeader, @RequestParam("active-filters") String[] activeFilters) {
        //return empty array if no filter is selected
        if (!(activeFilters.length > 0)) {
            return new ArrayList();
        }

        //decode token and get user/ team info
        String token = authorizationHeader.split(" ")[1];
        JWTokenInfo jwTokenInfo = jwTokenUtil.getInfo(token);
        User currentUser = this.userRepository.findById(jwTokenInfo.getUserId());
        Team currentTeam = this.teamRepositoryJPA.getTeam(jwTokenInfo.getTeam());

        UserTeam userTeam = this.userTeamRepositoryJPA.findByUserAndTeam(currentUser, currentTeam);

        boolean containsCategory = false;
        //main query for categories
        String queryString = "SELECT n FROM Note n WHERE n.shared = TRUE";

        //check for every category if it exists in the active filter list, if so we add it to the main query
        boolean containsBotany = Arrays.asList(activeFilters).contains("botany");
        if (containsBotany) {
            queryString += noteRepository.whereOrAndWhere(containsCategory, "n.noteCategory = 'BOTANY'");
            containsCategory = true;
        }

        //if a second category gets found we add it to the query with OR
        boolean containsAgronomy = Arrays.asList(activeFilters).contains("agronomy");
        if (containsAgronomy) {
            queryString += noteRepository.whereOrAndWhere(containsCategory, "n.noteCategory = 'AGRONOMY'");
            containsCategory = true;
        }

        boolean containsClimateScience = Arrays.asList(activeFilters).contains("climate-science");
        if (containsClimateScience) {
            queryString += noteRepository.whereOrAndWhere(containsCategory, "n.noteCategory = 'CLIMATE_SCIENCE'");
            containsCategory = true;
        }

        boolean containsHydrology = Arrays.asList(activeFilters).contains("hydrology");
        if (containsHydrology) {
            queryString += noteRepository.whereOrAndWhere(containsCategory, "n.noteCategory = 'HYDROLOGY'");
            containsCategory = true;
        }


        boolean containsGeology = Arrays.asList(activeFilters).contains("geology");
        if (containsGeology) {
            queryString += noteRepository.whereOrAndWhere(containsCategory, "n.noteCategory = 'GEOLOGY'");
            containsCategory = true;
        }

        //end query if any categories has been added
        if (containsCategory) {
            queryString += " ) ";
        }

        List<Note> notesList = new ArrayList();

        //if we added any category to our query we get the results and add it to the main notes list
        if (containsCategory) {
            List<Note> categoryNotes = this.noteRepository.createQuery(queryString).getResultList();
            notesList.addAll(categoryNotes);
        }

        //get team notes if selected from active filters
        boolean containsTeamNotes = Arrays.asList(activeFilters).contains("team-notes");
        if (containsTeamNotes) {
            //get all notes with relevant team id
            List<UserTeam> userTeamsForTeam = userTeamRepositoryJPA.findByTeamId(userTeam.getTeam());
            List<Long> userTeamIds = new ArrayList();

            for (UserTeam ut : userTeamsForTeam) {
                userTeamIds.add(ut.getId());
            }

            //make another query for team-notes
            List<Note> teamNotes = noteRepository
                    .createQuery("SELECT n FROM Note n WHERE n.createdBy.id IN (:userTeamIds) AND n.createdBy IS NOT NULL")
                    .setParameter("userTeamIds", userTeamIds)
                    .getResultList();
            //add team notes list to main list
            notesList.addAll(teamNotes);
        }
        //remove doubles, if multiple filters are selected
        return notesList.stream().distinct().collect(Collectors.toList());
    }


    //get notes by incoming id
    @GetMapping("/note-page/{id}")
    public ResponseEntity<Note> findNoteById(@PathVariable int id) {
        System.out.println("incoming id: " + id);
        Note note = noteRepository.findById(id);

        return note != null ? new ResponseEntity<>(note, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    //create note with incoming new note
    @PostMapping("/note-page")
    public ResponseEntity<Note> createNote(@RequestBody Note incomingNote) {
        System.out.println(incomingNote.getId());
        Note note = new Note(incomingNote.getCreatedBy(), incomingNote.getNoteCategory(), incomingNote.getTitle(), incomingNote.getContent(), incomingNote.isShared());
        System.out.println(note.getId());
        Note testNotes = noteRepository.save(note);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(testNotes.getId()).toUri();

        return ResponseEntity.created(location).body(testNotes);
    }


    //update incoming note with same note id from path variable
    @PutMapping("/note-page/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable int id, @RequestBody Note note) {
        if (id == note.getId()) {
            noteRepository.save(note);
            return ResponseEntity.ok(note);
        }

        //if note id doesnt match incoming id, throw precondition failed error
        return new ResponseEntity<>(note, HttpStatus.PRECONDITION_FAILED);
    }


    //delete note with incoming id
    @DeleteMapping("/note-page/{id}")
    public ResponseEntity<Note> deleteNoteById(@PathVariable int id) {
        Note note = noteRepository.deleteById(id);
        //if found note is empty, throw httpStatus not found
        return note != null ? new ResponseEntity<>(note, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

