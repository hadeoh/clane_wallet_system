package com.clane.wallet.dtos;

import com.clane.wallet.enums.Currency;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TopUpDto {

    @NotNull
    @NotBlank
    private String walletAccountNumber;

    @NotNull
    private Currency currency;

    @NotNull
    private Double topUpAmount;
}
