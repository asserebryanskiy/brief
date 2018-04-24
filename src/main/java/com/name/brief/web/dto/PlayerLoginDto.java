package com.name.brief.web.dto;

import com.name.brief.model.games.AuthenticationType;
import lombok.Data;

@Data
public class PlayerLoginDto {
    private String gameSessionStrId;
    private String name;
    private String surname;
    private String commandName;

    private AuthenticationType authenticationType;
}
