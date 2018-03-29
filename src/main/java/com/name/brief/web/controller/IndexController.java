package com.name.brief.web.controller;

import com.name.brief.model.Player;
import com.name.brief.service.PlayerAuthenticationService;
import com.name.brief.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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
        // if player is already authenticated redirect him to game
        Authentication authentication = (Authentication) principal;
        if (authentication != null && authentication.getPrincipal() instanceof Player
                && playerAuthenticationService.isLoggedIn((Player) authentication.getPrincipal())) {
            return "redirect:/game";
        }

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

    @RequestMapping("/.well-known/acme-challenge/H-zTGXNuknNuEDEa2rixZkv0mNFN_u72NO0IAozsBng")
    @ResponseBody
    public String sertificate() {
        return "H-zTGXNuknNuEDEa2rixZkv0mNFN_u72NO0IAozsBng.yaJ7SlCwIHaiDb-R461yRIaVEMru1jJgTSFlM8NlRSc";
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
