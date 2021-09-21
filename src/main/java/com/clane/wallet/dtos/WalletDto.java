package com.clane.wallet.dtos;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.models.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletDto {

    private User user;
    private Currency currency;
}
