package com.name.web.controller;

import com.name.model.Player;
import com.name.service.GameSessionService;
import com.name.validation.PlayerValidator;
import com.name.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;

@Controller
public class IndexController {

    private final PlayerValidator validator;
    private final GameSessionService gameSessionService;

    @Autowired
    public IndexController(PlayerValidator validator, GameSessionService gameSessionService) {
        this.validator = validator;
        this.gameSessionService = gameSessionService;
    }

    @InitBinder("player")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping("/")
    public String getMain(HttpServletRequest request, Model model) {
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession();
            addFlashAttribute(model, session, "flash");
            // these attributes come from com.name.config.authentication.PlayerAuthenticationFilter.class
            addFlashAttribute(model, session, "player");
            addFlashAttribute(model, session, "org.springframework.validation.BindingResult.player");
        }
        if (!model.containsAttribute("player")) {
            model.addAttribute("player", new Player());
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

    /*@RequestMapping(value = "/", method = RequestMethod.POST)
    public String login(@Valid Player player,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        if (result.hasErrors()) {
            if (result.hasFieldErrors("loggedIn")) {
                System.out.println("Already loggedIn");
                String username = (String) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                if (username.equals(getUsername(player))) return "redirect:/game";
            }
            redirectAttributes.addFlashAttribute("player", player);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.player", result);
            return "redirect:/";
        }
        gameSessionService.login(player);
        String username = getUsername(player);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_PLAYER"))));
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

//        redirectAttributes

        return "redirect:/login";
    }*/

    private String getUsername(Player player) {
        return player.getGameSession().getStrId() + LocalDate.now().toString() + player.getCommandName();
    }
}
