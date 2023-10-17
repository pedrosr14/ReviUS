package com.tfg.review.services;

import com.tfg.review.dtos.FullSLRDTO;
import com.tfg.review.dtos.ResearcherDTO;
import com.tfg.review.dtos.SLRDTO;
import com.tfg.review.dtos.UserDTO;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.SLR;

import java.util.List;
public interface SLRService {

    SLR findOne(Long slrId);
    List<SLR> findAll();
    SLRDTO createAndSave(SLRDTO dto, Long principalResearcherId);
    FullSLRDTO update(SLR slr);
    void delete(Long id);

    /**
     *
     * @param userId
     * @return A list with all the reviews associated to the same Researcher by its userID
     */
    List<SLR> findByResearcherUserId(Long userId);

    /**
     *
     * @param slrId the ID of the SLR to which we want to add the Researcher
     * @param researcherDTO the data of the User that will be Collaborator Researcher
     * @return the Researcher added
     */
    Researcher addResearcher(Long slrId, UserDTO researcherDTO, Long userId);

}
