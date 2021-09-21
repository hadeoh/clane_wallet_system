package com.clane.wallet.controllers;

import com.clane.wallet.dtos.KycLevelDto;
import com.clane.wallet.dtos.RegistrationDto;
import com.clane.wallet.models.User;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.responses.Response;
import com.clane.wallet.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
@Api("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ApiOperation(value = "Register a user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = Wallet.class)
    public ResponseEntity<Response<Wallet>> registerUser(
            @RequestBody @Valid RegistrationDto request) {
        Response<Wallet> response = userService.createUserAccount(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{userId}")
    @ApiOperation(value = "Upgrade user's kyc level",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = Wallet.class)
    public ResponseEntity<Response<User>> upgradeKycLevel(@RequestBody KycLevelDto request, @PathVariable("userId") Long userId) {
        Response<User> response = userService.upgradeKycLevel(request, userId);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
