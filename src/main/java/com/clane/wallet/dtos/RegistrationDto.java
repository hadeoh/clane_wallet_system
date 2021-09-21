package com.clane.wallet.dtos;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.utils.validations.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegistrationDto {

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    private String emailAddress;

    @NotNull
    @NotBlank
    @PhoneNumber
    private String phoneNumber;

    @NotNull
    private Currency walletCurrency;
}
