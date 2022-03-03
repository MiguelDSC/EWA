package app.note.restController.tests;
import app.models.UserTeam;
import app.models.note.Note;
import app.models.note.NoteCategory;
import app.models.user.Team;
import app.models.user.User;
import app.repositories.team.TeamRepositoryJPA;
import app.repositories.user.UserRepositoryJPA;
import app.repositories.userteam.UserTeamRepository;
import app.rest.note.NoteController;
import app.rest.security.JWTokenUtil;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestNoteRestController {
    private String token;
    private UserTeam userTeam;

    @Autowired
    public NoteController noteController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("teamRepositoryJPA")
    public TeamRepositoryJPA teamRepository;

    @Autowired
    @Qualifier("userTeamRepositoryJPA")
    public UserTeamRepository userTeamRepository;

    @Autowired
    @Qualifier("userRepositoryJPA")
    public UserRepositoryJPA userRepository;


    @Autowired
    private UserTeamRepository userTeamRepositoryJPA;


    @Autowired
    private JWTokenUtil jwTokenUtil;


    @Before
    public void createMockServlet() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    /**
     * Test to get the right http call when creating a note .
     */

    @Test
    public void testCreatingANoteShouldReturnNoteAndCorrectHTTPResponse() {

        //arange all info to create note
        User user = this.userRepository.findById(1);
        Team team = this.teamRepository.getTeam(1);
        UserTeam userTeam = this.userTeamRepository.findByUserAndTeam(user, team);

        //create note
        Note note = new Note(userTeam, NoteCategory.HYDROLOGY, "Test Title", "Test Content", false);

        //act get body from newly created note
        ResponseEntity<Note> newCreatedNote = this.noteController.createNote(note);

        //assert checking if response is correct
        assertEquals(newCreatedNote.getStatusCode(), HttpStatus.CREATED);
        assertEquals(note.getContent(), newCreatedNote.getBody().getContent());
        assertEquals(note.getNoteCategory(), newCreatedNote.getBody().getNoteCategory());
        assertEquals(note.isShared(), newCreatedNote.getBody().isShared());

        // Act: Cross-check results - was the note persisted?
        ResponseEntity<Note> queryResult = this.noteController.findNoteById(newCreatedNote.getBody().getId());

        // Assert: Check if data is correct
        assertEquals(queryResult.getStatusCode(), HttpStatus.OK);
        assertEquals(queryResult.getBody().getContent(), newCreatedNote.getBody().getContent());
        assertEquals(queryResult.getBody().getNoteCategory(), newCreatedNote.getBody().getNoteCategory());
        assertEquals(queryResult.getBody().isShared(), newCreatedNote.getBody().isShared());

    }


    /**
     * test if we get an empty list if no filters are selected.
     */
    @Test
    public void GetResponseFromGetNotesCall() throws Exception {

        //get all info to create token
        User user = userRepository.findById(1);
        Team team = teamRepository.getTeam(1);

        this.userTeam = userTeamRepositoryJPA.findByUserAndTeam(user, team);
        this.token = jwTokenUtil.encode(userTeam);

        // Act, set response with  active-filters includes team-notes
        RequestBuilder request = MockMvcRequestBuilders.get("/note-page/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).param("active-filters", "team-notes");
        MvcResult result = mockMvc.perform(request).andReturn();

        // Act, set the content as a new json object.
        String content = result.getResponse().getContentAsString();
        JSONArray jsonArray = new JSONArray(content);

        //assert that we get an array if we pass a filter
        assertThat(jsonArray).isNotNull();


        // Act, update response with no added filters
        request = MockMvcRequestBuilders.get("/note-page/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).param("active-filters", "");
        result = mockMvc.perform(request).andReturn();

        content = result.getResponse().getContentAsString();
        jsonArray = new JSONArray(content);


        //assert that if no filters are passed, we return an empty list
        assertThat(jsonArray.length()).isEqualTo(0);

    }
}
