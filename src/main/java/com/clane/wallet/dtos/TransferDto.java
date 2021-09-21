package com.clane.wallet.dtos;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.enums.TransferMeans;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TransferDto {

    private String destinationWalletAccountNumber;
    private String destinationEmail;
    @NotNull
    private TransferMeans means;
    @NotNull
    private Double amount;
    @NotNull
    @NotEmpty
    private String debitAccountNumber;
    @NotNull
    private Currency currency;

}
