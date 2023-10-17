package com.tfg.slr.usersmicroservice.services.implementations;

import com.tfg.slr.usersmicroservice.dtos.*;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.exceptions.ServiceDownException;
import com.tfg.slr.usersmicroservice.exceptions.UserNotFoundException;
import com.tfg.slr.usersmicroservice.models.AuthUser;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.repositories.UserRepository;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.UserService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAccountService userAccountService;
    private final AuthUserService authUserService;
    private final RestTemplate restTemplate;

    public List<User> findAll() {

        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        if(id==null) throw new IllegalArgumentException(MessageConstants.NULL_ENTITY);

        return userRepository.findUserById(id);
    }

    public Optional<User> findByEmail(String email) {
        if(email==null) throw new IllegalArgumentException(MessageConstants.WRONG_EMAIL);

        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserProfileDTO createAndSave(UserDTO userDTO){
        if(userDTO==null) throw new NullEntityException(MessageConstants.NULL_ENTITY);
        if(userRepository.existsByEmail(userDTO.getEmail())) throw new IllegalArgumentException(MessageConstants.EXISTING_EMAIL);

        UserAccount userAccount = authUserService.getAuthUserAccount();
        User user = UserDTO.createEntity(userDTO);

        user.setUserAccount(userAccount);
        userAccount.setUser(user);

        userRepository.save(user);
        userAccountService.update(userAccount);

        return new UserProfileDTO(user, userAccount);
    }

    public User update(User user){
        if(user==null) throw new NullEntityException(MessageConstants.NULL_ENTITY);
        if(!userRepository.existsByEmail(user.getEmail())) throw new UserNotFoundException(MessageConstants.USER_NULL_NOT_FOUND);

        return userRepository.save(user);
    }

    public void delete(Long userId){
        if(userId == null) throw new IllegalArgumentException(MessageConstants.NULL_ENTITY);

        User user = userRepository.findUserById(userId).orElseThrow(()-> new UserNotFoundException(MessageConstants.USER_NULL_NOT_FOUND));
        userRepository.delete(user);
    }
    //--Other methods--//

    public List<SLRDTO> getSLR(Long userId){
        if(userId==null) throw new IllegalArgumentException(MessageConstants.NULL_ENTITY);
        ParameterizedTypeReference<List<SLRDTO>> responseType = new ParameterizedTypeReference<List<SLRDTO>>() {};
        ResponseEntity<List<SLRDTO>> responseEntity = restTemplate.exchange(
                "http://review-service/api/review/byResearcherUserId/"+userId,
                HttpMethod.GET,
                null,
                responseType
        );
        return responseEntity.getBody();
    }

    public SLRDTO createSLR(ResearcherAndSLR fullDto, Long userId) {
        if(fullDto == null || userId == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        SLRDTO createdSLR = restTemplate.postForObject("http://review-service/api/review/19/create-review", fullDto, SLRDTO.class);
        return createdSLR;
    }

    public ResearcherDTO addCollaborator(String email, Long SLRid){
        if(email == null || SLRid == null) throw new NullEntityException(MessageConstants.NULL_ENTITY);

        User collaborator = userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException(MessageConstants.USER_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalArgumentException(MessageConstants.NO_USER_AUTHENTICATED);
        Object principal = auth.getPrincipal();

        AuthUser authUser = null;
        if (principal instanceof AuthUser) {
             authUser = (AuthUser) principal;
        }

        UserAccount loggedUserAccount = userAccountService.findByUserName(authUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException(MessageConstants.USER_NOT_FOUND));
        User loggedUser = loggedUserAccount.getUser();

        //check that the principal collaborates in the selected SLR
        List<SLRDTO> personalSLRs = this.getSLR(loggedUser.getId());
        boolean hasPermission = false;
        SLRDTO wantedSLR = null;
        for (SLRDTO slrdto : personalSLRs){
            if(Objects.equals(slrdto.getId(), SLRid)){
                hasPermission = true;
                wantedSLR = slrdto;
                break;
            }
        }

        if(!hasPermission) throw new IllegalArgumentException("No tienes acceso a esa Revisión");

        ResearcherAndSLR additionDTO = new ResearcherAndSLR(UserDTO.fromEntity(collaborator), wantedSLR);
        ResponseEntity<ResearcherDTO> response = restTemplate.exchange("http://review-service/api/review/researcher/add-to-SLR",
                HttpMethod.POST,
                new HttpEntity<>(additionDTO),
                ResearcherDTO.class);

        if (response.getStatusCodeValue() == 500){
            throw new ServiceDownException(MessageConstants.SERVICE_DOWN);
        } else if (response.getStatusCodeValue() == 200){
            return response.getBody();
        } else {
            throw new IllegalArgumentException("Error en la transacción");
        }
    }
}
