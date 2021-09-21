package com.clane.wallet.models;

import com.clane.wallet.enums.Currency;
import com.clane.wallet.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "transactions")
@Entity
@Data
public class Transaction extends AuditEntity implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Wallet wallet;

    @NotNull
    private Double amount;

    @NotNull
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull
    @NotBlank
    @Column(name = "cr_dr")
    private String crDr;

    @NotNull
    @Column(name = "starting_balance")
    private Double startingBalance;

    @NotNull
    @Column(name = "final_balance")
    private Double finalBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @NotNull
    @NotBlank
    @Column(name = "transaction_reference")
    private String transactionReference;
}
