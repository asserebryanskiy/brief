package com.name.brief.service;

import com.name.brief.model.Player;
import com.name.brief.repository.PlayerRepository;
import com.name.brief.web.dto.PlayerConnectionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.name.brief.web.dto.PlayerConnectionDto.*;

@Service
public class PlayerAuthenticationServiceImpl implements PlayerAuthenticationService {

    private final SimpMessagingTemplate template;
    private final PlayerRepository playerRepository;
    private SessionRegistry sessionRegistry;

    @Autowired
    public PlayerAuthenticationServiceImpl(SimpMessagingTemplate template,
                                           PlayerRepository playerRepository) {
        this.template = template;
        this.playerRepository = playerRepository;
    }

    @Override
    public Set<String> getAuthenticatedPlayersUsernames(Long gameSessionId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof Player
                        && isLoggedIn((Player) p)
                        && ((Player) p).getGameSession().getId().equals(gameSessionId))
                .map(p -> ((Player) p).getUsername())
                .collect(Collectors.toSet());
    }

    @Override
    public void logout(String username) {
        Player player = playerRepository.findByUsername(username);
        if (player != null) logout(player);
    }

    @Override
    public void logout(Player player) {
        sessionRegistry.getAllSessions(player, true)
                .forEach(sessionInformation -> {
                    if (!sessionInformation.isExpired()) {
                        sessionInformation.expireNow();
                    }
                    sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                });

        // send to moderator info about player logout
        sendToClient(PlayerConnectionInstruction.LOGOUT, player);

        // send to player instruction (if he is still in game) to return to index page
        template.convertAndSend("/queue/player/" + player.getId() + "/goToIndex", "");
    }

    @Override
    public boolean isLoggedIn(Player player) {
        return sessionRegistry.getAllSessions(
                player, false).size() > 0;
    }

    @Override
    public boolean isLoggedIn(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> ((Player) p).getUsername().equals(username) && isLoggedIn((Player) p))
                .findAny()
                .orElse(null) != null;
    }

    @Override
    public void setSessionRegistry(SessionRegistry registry) {
        this.sessionRegistry = registry;
    }

    private void sendToClient(PlayerConnectionDto.PlayerConnectionInstruction command, Player player) {
        Long gameSessionId = player.getGameSession().getId();
        String destination = "/queue/" + gameSessionId + "/connection";
        PlayerConnectionDto dto = new PlayerConnectionDto(command, player.getUsername());
        switch (player.getGameSession().getAuthenticationType()) {
            case COMMAND_NAME:
                dto.setIdentifierForModerator(player.getCommandName());
                break;
            default:
                dto.setIdentifierForModerator(String.valueOf(player.getId()));
        }
        template.convertAndSend(destination, dto);
    }
}
