package com.tfg.review.services;

import com.tfg.review.dtos.UserDTO;
import com.tfg.review.exceptions.ResearcherNotFoundException;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.Rol;
import com.tfg.review.utils.MessageConstants;

import java.util.HashSet;
import java.util.List;

public interface ResearcherService {

    List<Researcher> findAll();

    Researcher findOne(Long id);

    List<Researcher> findByUserId(Long id);

    /**
     *
     * @param user DTO with the info of the user that is going to be a Researcher
     * @param userId ID of the user that will be related to the Researcher object
     * @return a new Researcher associated to a User from the user-service
     */
    Researcher createFromUser(UserDTO user, Long userId);

    Researcher update(Long researcherId);
}
