package com.tfg.review.services.implementations;

import com.tfg.review.dtos.KeywordDTO;
import com.tfg.review.exceptions.KeywordNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.Keyword;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.KeywordRepository;
import com.tfg.review.services.KeywordService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;

    private final ProtocolServiceImpl protocolService;

    //--CRUD--//

    public Keyword createAndSave(KeywordDTO keywordDTO, Long protocolId){
        if(keywordDTO == null) throw new IllegalArgumentException("Keyword is null");

        Protocol father = protocolService.findOne(protocolId);
        Keyword result = KeywordDTO.buildEntity(keywordDTO);
             result.setProtocols(new HashSet<>());
             result.addProtocol(father);

        keywordRepository.save(result);

        father.addKeyword(result);
        protocolService.update(father);

       return result;
    }
    public List<Keyword> findAll() {

        return keywordRepository.findAll();
    }

    public Optional<Keyword> findOne(Long id){
        if(!keywordRepository.existsById(id)) throw new KeywordNotFoundException(MessageConstants.KEYWORD_NOT_FOUND);

        return keywordRepository.findKeywordById(id);
    }

    public Keyword update (Keyword keyword){
        if(keyword==null || !keywordRepository.existsById(keyword.getId())) throw new KeywordNotFoundException(MessageConstants.KEYWORD_NOT_FOUND);

        return keywordRepository.save(keyword);
    }

    public void deleteFromProtocol(Keyword keyword, Long protocolId){
        if(keyword==null || !keywordRepository.existsById(keyword.getId())) throw new KeywordNotFoundException(MessageConstants.KEYWORD_NOT_FOUND);
        if(protocolId==null) throw new IllegalArgumentException(MessageConstants.NULL_ID_ENTITY);

        Protocol father = protocolService.findOne(protocolId);
        father.removeKeyword(keyword);
        protocolService.update(father);
    }

    public void delete (Long keywordId){
        if(keywordId==null) throw new IllegalArgumentException(MessageConstants.NULL_ID_ENTITY);

        Keyword keyword = keywordRepository.findKeywordById(keywordId).orElseThrow(() -> new KeywordNotFoundException(MessageConstants.KEYWORD_NOT_FOUND));

        List<Protocol> protocols= List.copyOf(keyword.getProtocols());
        for(Protocol protocol: protocols) {
            protocol.removeKeyword(keyword);
            protocolService.update(protocol);
        }

        keywordRepository.delete(keyword);
    }

    //--Assistant methods--//

}
