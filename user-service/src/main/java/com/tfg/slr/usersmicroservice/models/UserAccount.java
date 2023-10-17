package com.tfg.slr.usersmicroservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import jakarta.validation.Valid;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="user_account")
public class UserAccount {

    //----Attributes-------//
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_account_id")
    private Long id;

    @NotBlank
    @Column(name="username", unique=true)
    private String userName;

    /*
    @Size(min = 8, max = 32) //Password must be minimum 8 characters and maximum 32
    @Pattern(regexp = "^(?=.*[ña-z])(?=.*[ÑA-Z])(?=.*\d)[ña-zÑA-Z\d]{8,32}$")  //Password also must contain, at least, a lowercase letter, an uppercase letter and a number
    */
    @JsonIgnore //Sending password data must be avoided
    @NotBlank
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name="is_admin")
    private Boolean isAdmin;


    //----Relationships-------//
    @JsonIgnore //This is necessary to break the infinite recursion
    @Valid
    @OneToOne(mappedBy = "userAccount", orphanRemoval = true)
    private User user;

}
