package com.name.brief.web.controller;

import com.name.brief.model.GameSession;
import com.name.brief.service.GameSessionService;
import com.name.brief.validation.GameSessionDtoValidator;
import com.name.brief.web.FlashMessage;
import com.name.brief.web.dto.GameSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class ModeratorController {

    private final GameSessionService service;
    private final GameSessionDtoValidator validator;

    @InitBinder("gameSessionDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @Autowired
    public ModeratorController(GameSessionService service, GameSessionDtoValidator validator) {
        this.service = service;
        this.validator = validator;
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
    public String getModeratorPanel(Model model) {
        List<GameSession> futureSessions = service.getFutureSessions();
        model.addAttribute("activeSessions", futureSessions);
        model.addAttribute("pastSessions", service.getPastSessions());
        if (!model.containsAttribute("gameSessionDto")) {
            model.addAttribute("gameSessionDto", new GameSessionDto());
        }
        Map<String, GameSessionDto> dtosMap = GameSessionDto.getDtosMap(futureSessions);
        if (model.containsAttribute("changeGameSessionDto")) {
            GameSessionDto changeDto = (GameSessionDto) model.asMap().get("changeGameSessionDto");
            dtosMap.put(changeDto.getOldStrId(), changeDto);
        }
        model.addAllAttributes(dtosMap);

        return "administration/moderator/index";
    }

    @RequestMapping(value = "/moderator/changeGameSession", method = RequestMethod.POST)
    public String changeSession(@Valid GameSessionDto dto,
                                BindingResult results,
                                RedirectAttributes attributes) {
        if (results.hasErrors()) {
            attributes.addFlashAttribute("changeGameSessionDto", dto);
            attributes.addFlashAttribute("showChangePopup", true);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.gameSessionDto", results);
            return "redirect:/moderator";
        }
        attributes.addFlashAttribute("flash", new FlashMessage("Игра успешно изменена!",
                FlashMessage.Status.SUCCESS));
        service.update(dto);
        return "redirect:/moderator";
    }

    @RequestMapping(value = "/moderator/deleteGameSession", method = RequestMethod.POST)
    public String deleteSession(HttpServletRequest request, RedirectAttributes attributes) {
        attributes.addFlashAttribute("flash", new FlashMessage("Игра успешно удалена!",
                FlashMessage.Status.SUCCESS));
        service.delete(Long.valueOf(request.getParameter("gameSessionId")));
        return "redirect:/moderator";
    }

    @RequestMapping(value = "/login/admin")
    public String getLoginPage(HttpServletRequest request, Model model) {
        if (request.getSession(false) != null) {
            model.addAttribute("flash", request.getSession().getAttribute("flash"));
            request.getSession().removeAttribute("flash");
        }
        return "administration/login";
    }
}
