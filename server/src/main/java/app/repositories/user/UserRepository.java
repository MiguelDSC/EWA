package app.repositories.user;

import app.models.user.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findById(long id);
    User findByEmail(String email);
    User save(User user);
    boolean deleteById(long id);
    int getUserLevel(long id, int team_id);
    List<User> getUsersByTeam(int team);
    int checkIfUserExistsByEmail(String email);
}
