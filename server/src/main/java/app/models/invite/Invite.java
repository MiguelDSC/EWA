package app.models.invite;

import app.models.UserTeam;
import app.models.user.Role;
import app.models.user.User;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

@Entity
public class Invite {
    @Id
    @GeneratedValue
    private long id;
    private String url;
    private String email;
    private LocalDate created;

    @OneToOne()
    @JoinColumn(name = "userTeam_id", referencedColumnName = "id")
    private UserTeam userTeam;

    public Invite() {}

    public Invite(UserTeam userTeam, boolean exist) throws NoSuchAlgorithmException {
        this.userTeam = userTeam;

        // Create hash
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Generate a SHA512 Hash, not the best but it'll function for now
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        String namesToHash = userTeam.getUser().getFirstname() + userTeam.getUser().getSurname();
        byte[] hashedPassword = md.digest(namesToHash.getBytes(StandardCharsets.UTF_8));

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String hashed = encoder.encodeToString(hashedPassword);

        if (exist) {
            this.setUrl("aaa" + hashed);
        } else {
            this.setUrl(hashed);
        }

        this.setEmail(this.userTeam.getUser().getEmail());
        this.setCreated(LocalDate.now());
    }

    public Invite(long id, String url, String email, LocalDate created) {
        this.id = id;
        this.url = url;
        this.email = email;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public UserTeam getUserTeam() {
        return userTeam;
    }

    public void setUserTeam(UserTeam userTeam) {
        this.userTeam = userTeam;
    }

}