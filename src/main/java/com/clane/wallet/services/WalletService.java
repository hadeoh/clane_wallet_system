package com.clane.wallet.services;

import com.clane.wallet.dtos.NewWalletDto;
import com.clane.wallet.dtos.WalletDto;
import com.clane.wallet.enums.WalletStatus;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.responses.Response;
import org.springframework.data.domain.Page;

public interface WalletService {

    Wallet generateWallet(WalletDto dto);
    Response<Wallet> createNewWallet(NewWalletDto dto);
    Response<Wallet> updateWalletStatus(Long id, WalletStatus status);
    Response<Page<Wallet>> findWallet(String walletAcctNumber, Integer pageNumber, Integer pageSize);
}
