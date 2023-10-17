package com.tfg.slr.usersmicroservice.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class TokenBlacklistService {

    private final Set<String> blacklist = new HashSet<>();

    /**
     * These methods help us to keep track of the tokens that has been revoked
     * It's useful specially to logout users
     */
    public void addToBlacklist(String token){
        blacklist.add(token);
    }

    public boolean isTokenRevoked(String token){
        return blacklist.contains(token);
    }

}
