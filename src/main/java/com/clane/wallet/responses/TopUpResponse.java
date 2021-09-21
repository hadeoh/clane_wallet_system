package com.clane.wallet.responses;

import com.clane.wallet.models.Transaction;
import com.clane.wallet.models.Wallet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopUpResponse {

    private Transaction transaction;
    private Wallet wallet;
}
