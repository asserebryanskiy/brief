package com.name.brief.web.controller;

import com.name.brief.model.Player;
import com.name.brief.model.Role;
import com.name.brief.model.User;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class IndexController {

    private final PlayerAuthenticationService playerAuthenticationService;

    @Autowired
    public IndexController(PlayerAuthenticationService playerAuthenticationService) {
        this.playerAuthenticationService = playerAuthenticationService;
    }

    @RequestMapping("/")
    public String getMain(HttpServletRequest request, Model model, Principal principal) {
        Authentication authentication = (Authentication) principal;
        if (authentication != null) {
            Object user = authentication.getPrincipal();
            // if player is already authenticated redirect him to game
            if (user instanceof Player
                    && playerAuthenticationService.isLoggedIn((Player) authentication.getPrincipal())) {
                return "redirect:/game";
            }
            // if moderator is authenticated redirect it to index
            if (user instanceof User && ((User) user).getRole().equals(Role.MODERATOR.getRole())) {
                return "redirect:/moderator";
            }
        }

        if (request.getSession(false) != null) {
            HttpSession session = request.getSession();
            // these attributes come from com.name.config.authentication.PlayerAuthenticationFilter.class
            addFlashAttribute(model, session, "flash");
            addFlashAttribute(model, session, "gameSessionStrId");
        }
        if (!model.containsAttribute("gameSessionStrId")) {
            model.addAttribute("gameSessionStrId", "");
        }

        return "index";
    }

    private void addFlashAttribute(Model model, HttpSession session, String attr) {
        Object obj = session.getAttribute(attr);
        if (obj != null) {
            model.addAttribute(attr, obj);
            session.removeAttribute(attr);
        }
    }
}
