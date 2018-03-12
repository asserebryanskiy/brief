package com.name.brief.web.controller;

import com.name.brief.service.GameSessionService;
import com.name.brief.validation.GameSessionDtoValidator;
import com.name.brief.web.FlashMessage;
import com.name.brief.web.dto.GameSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ModeratorController {

    private final GameSessionService service;
    private final GameSessionDtoValidator validator;
    private final SessionRegistry registry;

    @InitBinder("gameSessionDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @Autowired
    public ModeratorController(GameSessionService service, GameSessionDtoValidator validator, SessionRegistry registry) {
        this.service = service;
        this.validator = validator;
        this.registry = registry;
    }

    @RequestMapping(value = "/moderator", method = RequestMethod.POST)
    public String createNewGameSession(@Valid GameSessionDto dto,
                                       BindingResult results,
                                       RedirectAttributes attributes) {
        if (results.hasErrors()) {
            attributes.addFlashAttribute("gameSessionDto", dto);
            attributes.addFlashAttribute("showPopup", true);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.gameSessionDto", results);
            return "redirect:/moderator";
        }
        attributes.addFlashAttribute("flash", new FlashMessage("Игра успешно создана!",
                FlashMessage.Status.SUCCESS));
        service.save(dto.createGameSession());
        return "redirect:/moderator";
    }

    @RequestMapping("/moderator")
    public String getModeratorPanel(Model model, Principal principal) {
        String username = principal.getName();
        System.out.println(registry.getAllPrincipals());
        model.addAttribute("activeSessions", service.getFutureSessions());
        model.addAttribute("pastSessions", service.getPastSessions());
        model.addAttribute("username", username);
        if (!model.containsAttribute("gameSessionDto")) {
            model.addAttribute("gameSessionDto", new GameSessionDto());
        }

        return "administration/moderator/index";
    }
}
