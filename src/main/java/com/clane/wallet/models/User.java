package com.clane.wallet.models;

import com.clane.wallet.enums.KycLevel;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "users", indexes = @Index(columnList = "id, email_address"))
@Entity
@Data
public class User extends AuditEntity implements Serializable {

    @NotNull
    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotBlank
    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @NotNull
    @NotBlank
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "house_address")
    private String houseAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_level")
    private KycLevel kycLevel;

    @Column(name = "identity_doc_url")
    private String identityDocUrl;

    @Column(name = "utility_bill_url")
    private String utilityBillUrl;
}
