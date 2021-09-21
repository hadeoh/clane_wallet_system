package com.clane.wallet.dtos;

import com.clane.wallet.enums.KycLevel;
import lombok.Data;

@Data
public class KycLevelDto {

    private String houseAddress;
    private String identityDocUrl;
    private String utilityBillUrl;
    private KycLevel kycLevel;
}
