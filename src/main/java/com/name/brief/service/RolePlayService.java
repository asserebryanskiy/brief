package com.name.brief.service;

import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.web.dto.RolePlaySettingsDto;

public interface RolePlayService {
    void setUp(Long gameId, RolePlaySettingsDto dto) throws WrongGameTypeException;
}
