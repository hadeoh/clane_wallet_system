package com.clane.wallet.repositories;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUser_IdAndCurrency(Long userId, Currency currency);
    Wallet findByUser_EmailAddressAndCurrency(String email, Currency currency);
    Wallet findByWalletAccountNumberAndCurrency(String walletAccountNumber, Currency currency);
    Page<Wallet> findAllByWalletAccountNumberContaining(String walletAccountNumber, Pageable pageable);
}
