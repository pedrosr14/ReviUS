package com.tfg.slr.usersmicroservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
*This class is not an Entity, it's only a class that will have the data and privileges of the UserAccount of a User when it's authenticated
*Implements UserDetails, a class of Spring Security needed to grant permissions
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthUser implements UserDetails {

    /**
     * Only the data of UserAccount is needed
     * Also needs a collection with the authorities of the AuthUser
     *it must be generic due to the possibility of different type of authorities
     *and extends GrantedAuthority from Spring Security **/
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static AuthUser buildFromUserAccount(UserAccount userAccount){

        List<GrantedAuthority> authorities = new ArrayList<>();

        /*Now we need to add the roles converted to a SimpleGrantedAuthority object,
         * which are the objects that stores the authority granted to an Authentication object
         that we'll need later */

        if(userAccount.getIsAdmin()){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new AuthUser(userAccount.getUserName(), userAccount.getPassword(), authorities);
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }
    @Override
    public boolean isAccountNonLocked() {

        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }
    @Override
    public boolean isEnabled() {

        return true;
    }
}
