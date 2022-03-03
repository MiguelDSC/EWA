package app.models.user;

import app.models.UserTeam;
import app.models.invite.Invite;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String surname;
    private String email;
    private String imagePath;

    @JsonIgnore
    private String password;

    private static int incrementId = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<UserTeam> enteredTeam = new ArrayList<>();

    public User() {}
    public User(String firstName, String surname, String email) {
        this.firstname = firstName;
        this.surname = surname;
        this.email = email;
    }

    public User(String firstName, String surname, String role, String email,
                String imagePath, String password, String teamName) {
        this.firstname = firstName;
        this.surname = surname;
        this.email = email;
        this.imagePath = imagePath;
        this.password = password;
    }

    public static User createRandomUser(int id) {
        User user = new User();

        // Set the random user values (not really random but doesn't matter rn)
        user.setId(incrementId);
        user.setFirstname("Henk");
        user.setSurname("de Boer");
        user.setEmail("henk_boer@hotmail.nl");
        user.setPassword("sddjadadkjn");
        incrementId++;
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password_to_hash) {
        this.password = password_to_hash;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstname(String first_name) {
        this.firstname = first_name;
    }

    public void setSurname(String last_name) {
        this.surname = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImagePath(String image_path) {
        this.imagePath = image_path;
    }

    @Override
    public String toString() {
        return "UMSUser{" +
                "id=" + id +
                ", first_name='" + firstname + '\'' +
                ", last_name='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", image_path='" + imagePath + '\'' +
                ", password_to_hash='" + password + '\'' +
                '}';
    }
}
