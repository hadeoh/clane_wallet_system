package com.clane.wallet.controllers;

import com.clane.wallet.dtos.RegistrationDto;
import com.clane.wallet.dtos.TopUpDto;
import com.clane.wallet.dtos.TransferDto;
import com.clane.wallet.models.Transaction;
import com.clane.wallet.responses.Response;
import com.clane.wallet.responses.TopUpResponse;
import com.clane.wallet.services.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@Api("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ApiOperation(value = "Send money using wallet account number and email",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = Transaction.class)
    public ResponseEntity<Response<List<Transaction>>> transferFunds(@RequestBody @Valid TransferDto request) {
        Response<List<Transaction>> response = transactionService.transferFunds(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/topUp")
    @ApiOperation(value = "Top up wallet",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            response = Transaction.class)
    public ResponseEntity<Response<TopUpResponse>> topUpWallet(@RequestBody @Valid TopUpDto request) {
        Response<TopUpResponse> response = transactionService.topUpWallet(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
