package com.clane.wallet.services.impl;

import com.clane.wallet.dtos.TopUpDto;
import com.clane.wallet.dtos.TransferDto;
import com.clane.wallet.enums.TransactionType;
import com.clane.wallet.enums.TransferMeans;
import com.clane.wallet.enums.WalletStatus;
import com.clane.wallet.exceptions.CustomException;
import com.clane.wallet.models.Transaction;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.repositories.TransactionRepository;
import com.clane.wallet.repositories.UserRepository;
import com.clane.wallet.repositories.WalletRepository;
import com.clane.wallet.responses.Response;
import com.clane.wallet.responses.TopUpResponse;
import com.clane.wallet.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Response<List<Transaction>> transferFunds(TransferDto dto) {
        Response<List<Transaction>> response = new Response<>();
        if (dto.getAmount() <= 0) {
            throw new CustomException("Amount to be transferred has to be greater than 0", HttpStatus.BAD_REQUEST);
        }
        Wallet debitWallet = walletRepository.findByWalletAccountNumberAndCurrency(dto.getDebitAccountNumber(),
                dto.getCurrency());
        if (debitWallet == null) throw new CustomException("User does not have a(n) " + dto.getCurrency() + " wallet" +
                "or the wallet account number is invalid", HttpStatus.BAD_REQUEST);
        if (debitWallet.getStatus().equals(WalletStatus.INACTIVE)) throw new CustomException("Kindly activate wallet",
                HttpStatus.UNPROCESSABLE_ENTITY);
        Double transferLimit = debitWallet.getUser().getKycLevel().getWithdrawalLimit();
        if (transferLimit < dto.getAmount()) throw new CustomException(
                "The wallet holder cannot transfer more than " + transferLimit + ", kindly upgrade your kyc level",
                HttpStatus.BAD_REQUEST);
        switch (dto.getMeans().name()) {
            case "EMAIL":
                if (dto.getDestinationEmail() == null || dto.getDestinationEmail().isBlank()) throw new
                        CustomException("Destination email cannot be null or empty", HttpStatus.BAD_REQUEST);

                // Perform a check to know whether the user has a wallet and with that transfer currency
                Wallet destinationWallet = walletRepository.findByUser_EmailAddressAndCurrency(dto.getDestinationEmail(), dto.getCurrency());
                if (destinationWallet == null) throw new CustomException("User does not have a(n) " + dto.getCurrency() + " wallet" +
                        "or the email is invalid", HttpStatus.BAD_REQUEST);
                List<Transaction> transactions = processTransaction(dto, debitWallet, destinationWallet);
                response.setData(transactions);
                response.setMessage("Transaction completed successfully");
                response.setStatus(HttpStatus.CREATED);
                break;
            case "WALLET_ACCT_NUMBER":
                if (dto.getDestinationWalletAccountNumber() == null || dto.getDestinationWalletAccountNumber().isBlank())
                    throw new CustomException("Destination account number cannot be null or empty", HttpStatus.BAD_REQUEST);
                Wallet creditWallet = walletRepository.findByWalletAccountNumberAndCurrency(dto.getDestinationWalletAccountNumber(),
                        dto.getCurrency());
                if (creditWallet == null) throw new CustomException("User does not have a(n) " + dto.getCurrency() + " wallet" +
                        "or the email is invalid", HttpStatus.BAD_REQUEST);
                List<Transaction> transactionList = processTransaction(dto, debitWallet, creditWallet);
                response.setStatus(HttpStatus.CREATED);
                response.setData(transactionList);
                response.setMessage("Transaction completed successfully");
                break;
        }
        return response;
    }

    @Override
    @Transactional
    public Response<TopUpResponse> topUpWallet(TopUpDto dto) {
        Wallet wallet = walletRepository.findByWalletAccountNumberAndCurrency(dto.getWalletAccountNumber(), dto.getCurrency());
        if (wallet == null) throw new CustomException("User does not have a(n) " + dto.getCurrency() + " wallet" +
                "or the wallet account number is invalid", HttpStatus.BAD_REQUEST);
        Double newBalance = wallet.getBalance() + dto.getTopUpAmount();
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(newBalance);
        newBalance = bigDecimalAmount.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        Double startingBalance = wallet.getBalance();
        wallet.setBalance(newBalance);
        wallet = walletRepository.saveAndFlush(wallet);
        TransferDto transferDto = new TransferDto();
        transferDto.setAmount(dto.getTopUpAmount());
        transferDto.setCurrency(dto.getCurrency());
        Transaction transaction = buildTransactionRequest(transferDto, TransactionType.TOP_UP, "CR", wallet.getBalance(), startingBalance, wallet);
        transaction = transactionRepository.saveAndFlush(transaction);
        Response<TopUpResponse> response = new Response<>();
        response.setMessage("Top up was successful");
        response.setStatus(HttpStatus.CREATED);
        response.setData(TopUpResponse.builder().transaction(transaction).wallet(wallet).build());
        return response;
    }

    private List<Transaction> processTransaction(TransferDto dto, Wallet debitWallet, Wallet destinationWallet) {
        Double debitBalance = debitWallet.getBalance() - dto.getAmount();
        if (debitBalance < 0) throw new CustomException("Insufficient Balance", HttpStatus.UNPROCESSABLE_ENTITY);
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(debitBalance);
        debitBalance = bigDecimalAmount.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        Double debitStaringBalance = debitWallet.getBalance();
        debitWallet.setBalance(debitBalance);

        Double creditBalance = destinationWallet.getBalance() + dto.getAmount();
        bigDecimalAmount = BigDecimal.valueOf(creditBalance);
        creditBalance = bigDecimalAmount.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        Double creditStaringBalance = destinationWallet.getBalance();
        destinationWallet.setBalance(creditBalance);

        List<Wallet> walletList = new ArrayList<>();
        walletList.add(debitWallet);
        walletList.add(destinationWallet);
        List<Wallet> wallets = walletRepository.saveAll(walletList);

        Transaction debitTransaction = buildTransactionRequest(dto, TransactionType.TRANSFER,"DR", debitBalance, debitStaringBalance,
                wallets.get(0));

        Transaction creditTransaction = buildTransactionRequest(dto, TransactionType.TRANSFER, "CR", creditBalance, creditStaringBalance,
                wallets.get(1));

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(debitTransaction);
        transactionList.add(creditTransaction);
        return transactionRepository.saveAll(transactionList);
    }

    private Transaction buildTransactionRequest(TransferDto dto, TransactionType transactionType, String crDr,
                                                Double finalBalance, Double startingBalance, Wallet wallet) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setCrDr(crDr);
        transaction.setFinalBalance(finalBalance);
        transaction.setStartingBalance(startingBalance);
        transaction.setWallet(wallet);
        return transaction;
    }
}
