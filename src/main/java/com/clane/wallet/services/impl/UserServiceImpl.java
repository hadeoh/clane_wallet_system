package com.clane.wallet.services.impl;

import com.clane.wallet.dtos.KycLevelDto;
import com.clane.wallet.dtos.RegistrationDto;
import com.clane.wallet.dtos.WalletDto;
import com.clane.wallet.enums.Currency;
import com.clane.wallet.enums.KycLevel;
import com.clane.wallet.exceptions.CustomException;
import com.clane.wallet.models.User;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.repositories.UserRepository;
import com.clane.wallet.responses.Response;
import com.clane.wallet.services.UserService;
import com.clane.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<Wallet> createUserAccount(RegistrationDto dto) {
        Response<Wallet> response = new Response<>();

        //Check if a user exists in the db
        User existingUser = userRepository.findByPhoneNumberOrEmailAddress(dto.getPhoneNumber(), dto.getEmailAddress());
        if (existingUser != null) {
            response.setMessage("User already exists");
            response.setStatus(HttpStatus.CONFLICT);
        } else {
            User user = modelMapper.map(dto, User.class);
            user.setKycLevel(KycLevel.TIER_1);
            user = userRepository.saveAndFlush(user);
            Wallet wallet = walletService.generateWallet(WalletDto.builder()
                    .user(user)
                    .currency(dto.getWalletCurrency())
                    .build());
            response.setStatus(HttpStatus.CREATED);
            response.setData(wallet);
            response.setMessage("User successfully registered");
        }
        return response;
    }

    @Override
    public Response<User> upgradeKycLevel(KycLevelDto dto, Long userId) {
        Response<User> response = new Response<>();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User with id " + userId + " does not exist");
        } else {
            User user = optionalUser.get();
            if (user.getKycLevel().equals(dto.getKycLevel())) {
                response.setMessage("User is already on " + dto.getKycLevel());
                response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (dto.getKycLevel().equals(KycLevel.TIER_2)) {
                user = upgradeToTier2(user, dto);
                response.setStatus(HttpStatus.ACCEPTED);
                response.setMessage("User successfully upgraded to " + dto.getKycLevel());
                response.setData(user);
            } else if (dto.getKycLevel().equals(KycLevel.TIER_3)) {
                user = upgradeToTier3(user, dto);
                response.setStatus(HttpStatus.ACCEPTED);
                response.setMessage("User successfully upgraded to " + dto.getKycLevel());
                response.setData(user);
            }
        }
        return response;
    }

    private User upgradeToTier2(User user, KycLevelDto dto) {
        if (dto.getIdentityDocUrl() == null || dto.getIdentityDocUrl().isBlank()) {
            throw new CustomException("You have to provide at least identity document url for this user's kyc level " +
                    "to be upgraded", HttpStatus.BAD_REQUEST);
        } else {
            user.setIdentityDocUrl(dto.getIdentityDocUrl());
            user.setKycLevel(KycLevel.TIER_2);
            user = userRepository.saveAndFlush(user);
        }
        return user;
    }

    private User upgradeToTier3(User user, KycLevelDto dto) {
        if (dto.getHouseAddress() == null || dto.getHouseAddress().isBlank()) {
            throw new CustomException("You have to provide house address for this user's kyc level to be upgraded",
                    HttpStatus.BAD_REQUEST);
        } else if  (dto.getUtilityBillUrl() == null || dto.getUtilityBillUrl().isBlank()) {
            throw new CustomException("You have to provide utility document url for this user's kyc level to be upgraded",
                    HttpStatus.BAD_REQUEST);
        }
        if (user.getIdentityDocUrl() == null && dto.getIdentityDocUrl() == null) {
            throw new CustomException("Please provide identity doc url", HttpStatus.BAD_REQUEST);
        }
        if (dto.getIdentityDocUrl() == null) {
            dto.setIdentityDocUrl(user.getIdentityDocUrl());
        }
        user.setIdentityDocUrl(dto.getIdentityDocUrl());
        user.setUtilityBillUrl(dto.getUtilityBillUrl());
        user.setHouseAddress(dto.getHouseAddress());
        user.setKycLevel(KycLevel.TIER_3);
        user = userRepository.saveAndFlush(user);
        return user;
    }
}
