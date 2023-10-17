package com.tfg.review.services;

import com.tfg.review.dtos.KeywordDTO;
import com.tfg.review.models.Keyword;
import java.util.List;
import java.util.Optional;

public interface KeywordService {

    Keyword createAndSave(KeywordDTO keywordDTO, Long protocolId);

    List<Keyword> findAll();

    Optional<Keyword> findOne(Long id);

    Keyword update (Keyword keyword);

    void deleteFromProtocol(Keyword keyword, Long protocolId);

    void delete (Long keywordId);

}
