package com.clane.wallet.dtos;

import com.clane.wallet.enums.Currency;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewWalletDto {

    @NotNull
    private Long userId;
    @NotNull
    private Currency currency;
}
