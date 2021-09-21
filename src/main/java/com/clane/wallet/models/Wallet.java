package com.clane.wallet.models;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.enums.WalletStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "wallets")
@Entity
@Data
public class Wallet extends AuditEntity implements Serializable {

    @NotNull
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotNull
    private Double balance;

    @NotNull
    @NotBlank
    @Column(name = "wallet_account_number")
    private String walletAccountNumber;
}
