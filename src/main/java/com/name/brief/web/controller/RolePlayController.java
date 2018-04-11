package com.name.brief.web.controller;

import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.service.RolePlayService;
import com.name.brief.web.dto.RolePlaySettingsDto;
import org.codehaus.groovy.antlr.treewalker.CompositeVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller()
public class RolePlayController {

    private final RolePlayService rolePlayService;

    @Autowired
    public RolePlayController(RolePlayService rolePlayService) {
        this.rolePlayService = rolePlayService;
    }

    @MessageMapping("/rolePlay/{gameId}/rolePlaySettings")
    public void setRolePlaySettings(@DestinationVariable Long gameId,
                                    RolePlaySettingsDto dto) throws WrongGameTypeException {
        rolePlayService.setUp(gameId, dto);
    }
}
