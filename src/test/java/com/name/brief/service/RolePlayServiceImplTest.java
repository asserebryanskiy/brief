package com.name.brief.service;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.model.GameSession;
import com.name.brief.model.Player;
import com.name.brief.model.games.Brief;
import com.name.brief.model.games.roleplay.PlayerData;
import com.name.brief.model.games.roleplay.PlayerLocation;
import com.name.brief.model.games.roleplay.RolePlay;
import com.name.brief.repository.GameRepository;
import com.name.brief.utils.RolePlayUtils;
import com.name.brief.web.dto.InstructionsDto;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static com.name.brief.utils.RolePlayUtils.addPlayers;
import static com.name.brief.utils.RolePlayUtils.getPhaseNameByIndex;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringRunner.class)
@WebMvcTest(RolePlayService.class)
public class RolePlayServiceImplTest {

    @Autowired
    private RolePlayService service;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private SimpMessagingTemplate template;

    @Test
    public void setUp_changesRolePLayStrategyToProvidedInDto() throws Exception {
        RolePlay game = new RolePlay();
        when(gameRepository.findOne(game.getId())).thenReturn(game);
        RolePlaySettingsDto dto = new RolePlaySettingsDto();
        dto.setStrategy(1);

        service.setUp(game.getId(), dto);

        assertThat(game.getStrategyNumber(), is(dto.getStrategy()));
        verify(gameRepository).save(game);
    }

    @Test(expected = WrongGameTypeException.class)
    public void ifProvidedToSetUpGameIdIsNotOfRolePlayGameThrowsException() throws Exception {
        when(gameRepository.findOne(0L)).thenReturn(new Brief());

        service.setUp(0L, null);
    }

    @Test
    public void changePhase_onSendRolesAddsPlayersFromGameSessionToGame() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, true);

        service.changePhase(getPhaseIndexByName("SEND_ROLES"), game.getId());

        assertThat(game.getPlayersData(), hasSize(4));
    }

    @Test
    public void changePhase_onSendRolesSendsInstructionsToPlayers() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, true);

        service.changePhase(getPhaseIndexByName("SEND_ROLES"), game.getId());

        for (int i = 0; i < 4; i++)
            verify(template, times(1)).convertAndSend(
                    eq("/queue/rolePlay/player/" + i + "/instructions"),
                    any(InstructionsDto.class)
            );
    }

    @Test
    public void changePhase_setsPhaseIndexOfGameToNewValue() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = new RolePlay();
        when(gameRepository.findOne(0L)).thenReturn(game);

        service.changePhase(1, 0L);

        assertThat(game.getPhaseIndex(), is(1));
    }

    @Test
    public void changePhase_onCrossing1SendsLocationObjectsToPlayers() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, false);

        service.changePhase(getPhaseIndexByName("CROSSING"), game.getId());

        for (int i = 0; i < 4; i++)
            verify(template, times(1)).convertAndSend(
                    eq("/queue/rolePlay/player/" + i + "/crossing"),
                    any(PlayerLocation.class)
            );
    }

    @Test
    public void changePhase_onCrossing2SendsLocationObjectsToSalesmans() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, false);

        service.changePhase(getPhaseIndexByName("CROSSING_2"), game.getId());

        verify(template, times(1)).convertAndSend(
                eq("/queue/rolePlay/player/0/crossing"),
                any(PlayerLocation.class)
        );
        verify(template, times(1)).convertAndSend(
                eq("/queue/rolePlay/player/2/crossing"),
                any(PlayerLocation.class)
        );
    }

    @Test
    public void changePhase_onCrossing2ChangesLocationOfSalesmans() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, false);

        service.changePhase(getPhaseIndexByName("CROSSING_2"), game.getId());

        assertThat(game.getPlayersData().get(0).getLocation().getRoom(), is(1));
        assertThat(game.getPlayersData().get(2).getLocation().getRoom(), is(0));
    }

    @Test
    public void changePhase_onSurveyPhaseSendsProperCommandToEveryRole() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, false);

        service.changePhase(getPhaseIndexByName("SURVEY"), game.getId());

        for (int i = 0; i < 4; i++) {
            verify(template, times(1)).convertAndSend(
                    "/queue/rolePlay/player/" + i + "/changePhase",
                    game.getPlayersData().get(i).getRole().isDoctorRole() ? "SURVEY_DOCTOR" : "SURVEY_SALESMAN"
            );
        }

    }

    @Test
    public void changePhase_sendsChangePhaseMessageToGameTopic() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, true);

        for (int i = 1; i < RolePlay.phases.size(); i++) {
            service.changePhase(i, game.getId());

            verify(template, times(1)).convertAndSend(
                    "/topic/game/" + game.getId() + "/changePhase",
                    getPhaseNameByIndex(i)
            );
        }
    }

    @Test
    public void changePhase_savesAllChangesHappenedToGameToDatabase() throws OddNumberOfPlayersException, WrongGameTypeException {
        RolePlay game = prepareForPhaseChangeTest(4, true);

        for (int i = 1; i < RolePlay.phases.size(); i++) {
            service.changePhase(i, game.getId());
        }

        verify(gameRepository, times(RolePlay.phases.size() - 1)).save(game);
    }

    private RolePlay prepareForPhaseChangeTest(int size, boolean sendRolesPhaseTest) throws OddNumberOfPlayersException {
        GameSession session = new GameSession();
        RolePlay game = new RolePlay();
        game.setId(0L);
        game.setGameSession(session);
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            player.setId(i);
            session.getPlayers().add(player);
        }
        if (!sendRolesPhaseTest) addPlayers(session.getPlayers(), game);
        when(gameRepository.findOne(0L)).thenReturn(game);
        return game;
    }

    private int getPhaseIndexByName(String name) {
        return RolePlay.phases.stream()
                .filter(phase -> phase.getEnglishName().equals(name))
                .findAny()
                .orElseThrow(IllegalArgumentException::new)
                .getOrderIndex();
    }
}