package com.name.brief.web.controller;

import com.name.brief.model.games.AuthenticationType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLoginController {
    @RequestMapping("/login")
    public String getPlayerDetailsPage(HttpServletRequest request, Model model) {
        if (request.getSession(false) == null
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

        return "playerDetails";
    }
}
