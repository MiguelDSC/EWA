package app.repositories.invite;

import app.models.invite.Invite;

import java.util.List;

public interface InviteInterface {
    List<Invite> findAll();
    Invite findById(long id);
    Invite save(Invite invite);
    boolean deleteById(long id);
}
