package com.clane.wallet.services;

import com.clane.wallet.dtos.TopUpDto;
import com.clane.wallet.dtos.TransferDto;
import com.clane.wallet.models.Transaction;
import com.clane.wallet.responses.Response;
import com.clane.wallet.responses.TopUpResponse;

import java.util.List;

public interface TransactionService {
    Response<List<Transaction>> transferFunds(TransferDto dto);
    Response<TopUpResponse> topUpWallet(TopUpDto dto);
}
