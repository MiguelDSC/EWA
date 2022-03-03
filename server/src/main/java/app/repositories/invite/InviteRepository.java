package app.repositories.invite;

import app.models.UserTeam;
import app.models.user.Team;
import app.models.user.User;
import app.models.invite.Invite;
import app.repositories.team.TeamRepositoryJPA;
import app.repositories.user.UserRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */
@PersistenceContext
@Repository
@Transactional
public class InviteRepository implements InviteInterface {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserRepositoryJPA umsRepositoryJPA;

    @Autowired
    private TeamRepositoryJPA teamRepositoryJPA;

    @Override
    public List<Invite> findAll() {
        List<Invite> invites = em.createQuery("FROM Invite").getResultList();

        return invites;
    }

    @Override
    public Invite findById(long id) {
        return this.em.find(Invite.class, id);
    }

    @Override
    public Invite save(Invite invite) {
        Invite result = em.merge(invite);

        return result;
    }

    @Override
    public boolean deleteById(long id) {
        User trashModel = em.find(User.class,id);

        if (trashModel != null) {
            em.remove(trashModel);
            return true;
        }

        return false;
    }

    /**
     * Delete invite
     */
    public boolean deleteInvite(Invite invite) {
        em.remove(invite);
        return true;
    }

    public Invite findByHash(String hash) {
        Invite inviteToReturn = null;
        for (Invite invite: this.findAll()) {
            if (invite.getUrl().equals(hash)) {
                inviteToReturn = invite;
            }
        }
        return inviteToReturn;
    }

    // Delete by hash
    public boolean deleteByHash(String hash) {
        for (Invite invite: this.findAll()) {
            if (invite.getUrl().equals(hash)) {
                this.deleteById(invite.getId());
                return true;
            }
        }
        return false;
    }

    // Delete by user
    public boolean deleteInviteByUser(UserTeam userTeam) {
        int count = em.createQuery("DELETE FROM Invite i WHERE i.userTeam = :u")
                .setParameter("u", userTeam).executeUpdate();

        return count > 0;
    }

    /**
     * Find all the invites for a team
     * @return
     */
    public List<String> findInvitesByTeam(int teamId) {
        Team team = teamRepositoryJPA.getTeam(teamId);
        List<String> invites = em.createQuery("SELECT i.email FROM Invite i JOIN UserTeam ut " +
                        "ON ut.id = i.userTeam.id WHERE ut.team = ?1")
                .setParameter(1, team).getResultList();
        return invites;
    }

}