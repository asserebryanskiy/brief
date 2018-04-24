package com.name.brief.web.controller;

import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.validation.PlayerLoginDtoValidator;
import com.name.brief.web.dto.PlayerLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;

@Controller
public class PlayerLoginController {

    private final PlayerLoginDtoValidator validator;
    private final GameSessionService gameSessionService;
    private final PlayerAuthenticationService playerAuthenticationService;

    @InitBinder("playerLoginDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @Autowired
    public PlayerLoginController(PlayerLoginDtoValidator validator,
                                 GameSessionService gameSessionService,
                                 PlayerAuthenticationService playerAuthenticationService) {
        this.validator = validator;
        this.gameSessionService = gameSessionService;
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getPlayerDetailsPage(Model model) {
        if (!model.containsAttribute("playerLoginDto")) return "redirect:/";
        return "playerDetails";
    }

    @RequestMapping(value = "/playerLogin", method = RequestMethod.POST)
    public String getPlayerDetailsPage(@Valid PlayerLoginDto dto,
                                       BindingResult result,
                                       RedirectAttributes attributes,
                                       HttpServletRequest request) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("playerLoginDto", dto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.playerLoginDto", result);
            if (result.hasFieldErrors("gameSessionStrId")) {
                return "redirect:/";
            }

            // if gameSessionStrID is correct set authentication type
            if (dto.getAuthenticationType() == null) {
                dto.setAuthenticationType(gameSessionService.getSession(
                        dto.getGameSessionStrId(), LocalDate.now()).getAuthenticationType());
            }
            return "redirect:/login";
        }

        playerAuthenticationService.authenticate(dto, request);

        return "redirect:/game";
    }
}
