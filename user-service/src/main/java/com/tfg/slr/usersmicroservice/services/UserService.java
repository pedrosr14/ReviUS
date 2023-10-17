package com.tfg.slr.usersmicroservice.services;

import com.tfg.slr.usersmicroservice.dtos.*;
import com.tfg.slr.usersmicroservice.models.User;


import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    UserProfileDTO createAndSave(UserDTO userDTO);

    User update(User user);

    void delete(Long userId);

    //--Other methods--//
    /**
     * Sends the info of the user that is creating the SLR to the Review Service
     *
     * @param fullDto: DTO with the data of the User as principal researcher
     *               and the data of the SLR that must be created
     * @param userId: the ID of the user that must be stored by the Review Service to track the user stored as a researcher
     * @return The SLR stored by the Review Service
     */
    SLRDTO createSLR(ResearcherAndSLR fullDto, Long userId);

    /**
     *
     * @param userId: the ID of the user from which we want to retrieve the list of SLR
     * @return the List of the SLRs where the user is a Researcher
     */
    List<SLRDTO> getSLR(Long userId);

    /**
     *
     * @param email the email of the Researcher we want to add to our SLR
     * @param SLRid the SLR we have
     * @return a ResearcherDTO with the data of the new collaborator
     */
    ResearcherDTO addCollaborator(String email, Long SLRid);
}
