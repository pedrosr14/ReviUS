package com.tfg.review.repositories;

import com.tfg.review.models.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findKeywordByKeyword(String keyword);

    Optional<Keyword> findKeywordById(Long id);

    boolean existsKeywordByKeyword(String keyword);

}