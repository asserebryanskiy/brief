package com.name.brief.service;

import com.name.brief.exception.OddNumberOfPlayersException;
import com.name.brief.exception.WrongGameTypeException;
import com.name.brief.web.dto.DoctorAnswerDto;
import com.name.brief.web.dto.DrugDistributionDto;
import com.name.brief.web.dto.RolePlaySettingsDto;
import com.name.brief.web.dto.SalesmanAnswerDto;

public interface RolePlayService {
    void setUp(Long gameId, RolePlaySettingsDto dto) throws WrongGameTypeException;

    void changePhase(int phaseIndex, Long gameId) throws WrongGameTypeException, OddNumberOfPlayersException;

    void saveDoctorAnswers(Long gameId, DoctorAnswerDto dto, Long playerId) throws WrongGameTypeException, OddNumberOfPlayersException;

    void saveSalesmanAnswers(Long gameId, SalesmanAnswerDto dto, Long playerId) throws WrongGameTypeException;

    void saveDrugDistribution(Long gameId, DrugDistributionDto dto, Long id) throws WrongGameTypeException;

    void add30sec(Long gameId);
}
