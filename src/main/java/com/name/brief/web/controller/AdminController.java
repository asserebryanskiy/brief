package com.name.brief.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminController {
    @RequestMapping(value = "/login/admin")
    public String getLoginPage(HttpServletRequest request, Model model) {
        if (request.getSession(false) != null) {
            model.addAttribute("flash", request.getSession().getAttribute("flash"));
            request.getSession().removeAttribute("flash");
        }
        return "administration/login";
    }

    @RequestMapping("/admin")
    public String getAdminPanel() {
        return "administration/admin/index";
    }
}
