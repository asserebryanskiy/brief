package com.name.model;

import com.name.games.brief.Brief;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(exclude = "decisions")    // to overcome stackOverflow exception
@Entity
public class Player extends BaseEntity implements UserDetails{
    public static final String ROLE = "ROLE_PLAYER";

    @ManyToOne
    @NotNull
    private GameSession gameSession;
    @NotNull
    private String commandName;
    private boolean loggedIn;
    private String currentAnswer;
    @OneToMany(mappedBy = "player")
    private List<Decision> decisions;
    private String username;

    public Player() {
        super();
        decisions = new ArrayList<>(Brief.NUMBER_OF_STAGES);
    }

    public Player(GameSession gameSession, String commandName) {
        this();
        this.gameSession = gameSession;
        this.commandName = commandName;
        username = constructUsername(gameSession.getStrId(), gameSession.getActiveDate(), commandName);
//        role = "ROLE_COMMAND";
    }

    public static String constructUsername(String strId, LocalDate activeDate, String commandName) {
        return String.format("%s%s%s", strId, activeDate.toString(), commandName);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>(1);
        authorities.add(new SimpleGrantedAuthority(ROLE));
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
