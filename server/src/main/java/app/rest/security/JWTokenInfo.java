package app.rest.security;

import app.models.user.Team;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static app.rest.security.JWTokenUtil.getKey;

public class JWTokenInfo {

    // Token information.
    private Integer id;
    private Integer role;
    private Integer team;
    private Date issuedAt;
    private Date expiration;
    private Integer userId;
    private Integer userTeamId;

    public JWTokenInfo(Integer id, Integer role, Integer team, Date issuedAt, Date expiration, Integer userId, Integer userTeamId) {
        this.id = id;
        this.role = role;
        this.team = team;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.userId = userId;
        this.userTeamId = userTeamId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRole() {
        return role;
    }

    public Integer getTeam() {
        return team;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public Date getExpiration() {
        return expiration;
    }

    public Integer getUserTeamId() {
        return userTeamId;
    }

    public Date getExpirationDate() {
        return expiration;
    }
}
