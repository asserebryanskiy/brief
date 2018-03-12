package com.name.brief.model;

import com.name.brief.config.SecurityConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Decision> decisions;
    private String username;

    public Player() {
        super();
        decisions = new ArrayList<>();
    }

    public Player(GameSession gameSession, String commandName) {
        this();
        this.gameSession = gameSession;
        this.commandName = commandName;
        username = constructUsername(gameSession.getStrId(), gameSession.getActiveDate(), commandName);
        int numberOfRounds = gameSession.getGame().getNumberOfRounds();
        decisions = new ArrayList<>(numberOfRounds);
        IntStream.range(0, numberOfRounds)
                .forEach(i -> decisions.add(new Decision(this, i, null)));
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
        return SecurityConfig.passwordEncoder.encode("password");
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

    public Decision getDecision(int roundNumber) {
        return decisions.stream()
                .filter(decision -> decision != null &&
                        decision.getRoundNumber() == roundNumber/* + 1*/)
                .findAny()
                .orElse(null);
    }

    public int getScoreForRound(int roundNumber) {
        Decision decision = getDecision(roundNumber);
        if (decision != null) {
            return gameSession.getGame().getScore(decision);
        }
        return 0;
    }

    public int getTotalScore() {
        return decisions.stream()
                .filter(Objects::nonNull)
                .mapToInt(d -> gameSession.getGame().getScore(d))
                .sum();
    }
}
