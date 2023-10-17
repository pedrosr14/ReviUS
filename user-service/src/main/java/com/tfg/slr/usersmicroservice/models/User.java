package com.tfg.slr.usersmicroservice.models;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="user")
public class User {

    //----Attributes-------//
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long id;

    @NotBlank
    @Column(name="name")
    private String completeName;

    @Column(name="work_field")
    @NotBlank
    private String workField;

    @Column(name="institution")
    private String institution;

    @Column(name = "email",  unique=true)
    @Email
    @NotBlank
    private String email;

    //--------Relationships---------//
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_account_id", nullable = false)
    private UserAccount userAccount;
}
