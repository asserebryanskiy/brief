package com.name.brief.web.controller;

import com.name.brief.model.Player;
import com.name.brief.service.GameSessionService;
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

    @InitBinder("playerLoginDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @Autowired
    public PlayerLoginController(PlayerLoginDtoValidator validator,
                                 GameSessionService gameSessionService) {
        this.validator = validator;
        this.gameSessionService = gameSessionService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getPlayerDetailsPage(Model model) {
        if (!model.containsAttribute("dto")) return "redirect:/";
        return "playerDetails";
    }

    @RequestMapping(value = "/playerLogin", method = RequestMethod.POST)
    public String getPlayerDetailsPage(@Valid PlayerLoginDto dto,
                                       BindingResult result,
                                       RedirectAttributes attributes,
                                       HttpServletRequest request) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("dto", dto);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.playerLoginDto", result);
            if (result.hasFieldErrors("gameSessionStrId")) {
                return "redirect:/";
            }

            // if gameSessionStrID is correct set authentication type
            dto.setAuthenticationType(
                    gameSessionService.getSession(dto.getGameSessionStrId(), LocalDate.now()).getAuthenticationType());
            return "redirect:/login";
        }

        if (request.getParameter("gameSessionStrId").equals("brief")) {
            try {
                request.login(Player.constructUsername("brief", LocalDate.now(), "1"),
                        "");
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/game";

        /*if (request.getSession(false) == null
                || request.getSession().getAttribute("gameSessionStrId") == null
                || request.getSession().getAttribute("authenticationType") == null) {
            return "redirect:/";
        }

        model.addAttribute("strId", request.getSession().getAttribute("gameSessionStrId"));
        Map<String, String> inputs = new HashMap<>();
        switch ((AuthenticationType) request.getAttribute("authenticationType")) {
            case COMMAND_NAME:
                inputs.put("commandName", "Название команды");
                break;
            case NAME:
                inputs.put("name", "Ваше имя");
                break;
            case NAME_SURNAME:
                inputs.put("name", "Ваше имя");
                inputs.put("surname", "Ваша фамилия");
                break;
        }
        model.addAttribute("inputs", inputs);

        return "playerDetails";*/
    }
}
