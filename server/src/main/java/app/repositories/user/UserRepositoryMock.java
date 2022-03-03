package app.repositories.user;

import app.models.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */
@Component
public class UserRepositoryMock implements UserRepository {

    private List<User> users;

    public UserRepositoryMock() {
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(User.createRandomUser(i));
        }
    }

    @Override
    public List<User> findAll() {
        return this.users;
    }

    @Override
    public User findById(long id) {
        User umsUser = null;
        for (User user: this.findAll()) {
            if (user.getId() == id) {
                umsUser = user;
            }
        }
        return umsUser;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public User save(User umsUser) {
        User newUser = new User();
        // Setters
        if ( umsUser.getId() != null && umsUser.getId() > 0) {
            User user = findById(umsUser.getId());
            users.set(users.indexOf(user), umsUser);
            return umsUser;
        }

        newUser.setId(users.get(users.size() - 1).getId() + 1);
        newUser.setFirstname(umsUser.getFirstname());
        newUser.setSurname(umsUser.getSurname());
        newUser.setEmail(umsUser.getEmail());
        newUser.setImagePath(umsUser.getImagePath());
        newUser.setPassword(umsUser.getPassword());

        users.add(newUser);

        return newUser;
    }

    @Override
    public boolean deleteById(long id) {
        for (User user: this.findAll()) {
            if (user.getId() == id) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getUserLevel(long id, int team_id) {
        return 0;
    }

    @Override
    public List<User> getUsersByTeam(int team) {
        return null;
    }

    @Override
    public int checkIfUserExistsByEmail(String email) {
        return 0;
    }
}