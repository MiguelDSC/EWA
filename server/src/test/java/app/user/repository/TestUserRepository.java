package app.user.repository;

import app.models.user.User;
import app.repositories.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestUserRepository {

    @Autowired
    @Qualifier("userRepositoryJPA")
    private UserRepository userRepository;

    @Before
    public void addData() {
        userRepository.save(new User("Rick", "De boer", "rick@hotmail.com"));
        userRepository.save(new User("Joost", "De boer", "joost@hotmail.com"));
        userRepository.save(new User("Gerard", "De boer", "gerard@hotmail.com"));
    }

    /**
     * Test if you can save an user
     */
    @Test
    public void testSavingUser() {
        User user = new User();

        user.setFirstname("Jolanda");

        userRepository.save(user);
    }

    /**
     * Test if you can get all the users.
     */
    @Test
    public void getAll() {
        List<User> users = userRepository.findAll();
        assertThat(users).isNotNull();
    }


    /**
     * Test if a user can still be found
     */
    @Test
    public void findUser() {
        User user = userRepository.findById(1);
        assertThat(user.getId()).isEqualTo(1);
    }
}