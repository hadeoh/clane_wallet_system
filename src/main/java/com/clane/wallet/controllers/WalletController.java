package com.clane.wallet.controllers;

import com.clane.wallet.dtos.NewWalletDto;
import com.clane.wallet.dtos.RegistrationDto;
import com.clane.wallet.enums.WalletStatus;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.responses.Response;
import com.clane.wallet.services.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/wallet")
@Api("/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @ApiOperation(value = "Create new wallet for an existing user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = Wallet.class)
    public ResponseEntity<Response<Wallet>> createNewWallet(
            @RequestBody @Valid NewWalletDto request) {
        Response<Wallet> response = walletService.createNewWallet(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Update the status of an existing wallet",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = Wallet.class)
    public ResponseEntity<Response<Wallet>> updateWalletStatus(@PathVariable("id") Long walletId,
                                                               @RequestParam("status") WalletStatus status) {
        Response<Wallet> response = walletService.updateWalletStatus(walletId, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    @ApiOperation(value = "Find wallets",
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = Wallet.class)
    public ResponseEntity<Response<Page<Wallet>>> findWallet(@RequestParam("walletAcctNumber") String walletAcctNumber,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size) {
        Response<Page<Wallet>> response = walletService.findWallet(walletAcctNumber, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
