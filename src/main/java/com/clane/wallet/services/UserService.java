package com.clane.wallet.services;

import com.clane.wallet.dtos.KycLevelDto;
import com.clane.wallet.dtos.RegistrationDto;
import com.clane.wallet.models.User;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.responses.Response;

public interface UserService {

    Response<Wallet> createUserAccount(RegistrationDto dto);
    Response<User> upgradeKycLevel(KycLevelDto dto, Long userId);
}
