package com.name.brief.repository;

import com.name.brief.model.games.roleplay.PlayerData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDataRepository extends CrudRepository<PlayerData, Long> {
}
