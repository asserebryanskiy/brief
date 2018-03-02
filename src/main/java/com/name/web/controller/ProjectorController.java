package com.name.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProjectorController {
    @RequestMapping("/projector")
    public String getProjectorView() {
        return null;
    }
}
