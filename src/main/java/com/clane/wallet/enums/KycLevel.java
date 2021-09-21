package com.clane.wallet.enums;

public enum KycLevel {
    TIER_1(20000.00), TIER_2(50000.00), TIER_3(100000.00);

    private final Double withdrawalLimit;

    KycLevel(Double withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }

    public Double getWithdrawalLimit() {
        return this.withdrawalLimit;
    }
}
