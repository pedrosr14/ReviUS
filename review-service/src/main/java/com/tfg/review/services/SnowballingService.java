package com.tfg.review.services;

import com.tfg.review.dtos.SnowballingDTO;
import com.tfg.review.models.Snowballing;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface SnowballingService {

    List<Snowballing> findAll();

    Optional<Snowballing> findOne(Long id);

    SnowballingDTO createAndSave(SnowballingDTO dto, Long protocolId);

    Snowballing update(Snowballing snowballing);

    void deleteFromProtocol(Long snowballingId, Long protocolId);
}
