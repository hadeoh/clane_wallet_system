package com.clane.wallet.services.impl;

import com.clane.wallet.dtos.NewWalletDto;
import com.clane.wallet.dtos.WalletDto;
import com.clane.wallet.enums.WalletStatus;
import com.clane.wallet.models.User;
import com.clane.wallet.models.Wallet;
import com.clane.wallet.repositories.UserRepository;
import com.clane.wallet.repositories.WalletRepository;
import com.clane.wallet.responses.Response;
import com.clane.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Wallet generateWallet(WalletDto dto) {
        Wallet wallet = modelMapper.map(dto, Wallet.class);
        wallet.setBalance(0.00);
        wallet.setStatus(WalletStatus.INACTIVE);
        wallet.setWalletAccountNumber(generateWalletAccountNumber());
        walletRepository.saveAndFlush(wallet);
        return wallet;
    }

    @Override
    public Response<Wallet> createNewWallet(NewWalletDto dto) {
        Response<Wallet> response = new Response<>();
        Optional<User> user = userRepository.findById(dto.getUserId());
        if (user.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User with id " + dto.getUserId() + " does not exist");
        } else {
            Wallet existingWallet = walletRepository.findByUser_IdAndCurrency(dto.getUserId(), dto.getCurrency());
            if (existingWallet != null) {
                response.setMessage("You already have a wallet in " + dto.getCurrency() + " currency");
                response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            } else {
                Wallet wallet = generateWallet(WalletDto.builder().currency(dto.getCurrency()).user(user.get()).build());
                response.setData(wallet);
                response.setMessage("New wallet successfully created");
                response.setStatus(HttpStatus.CREATED);
            }
        }
        return response;
    }

    @Override
    public Response<Wallet> updateWalletStatus(Long id, WalletStatus status) {
        Response<Wallet> response = new Response<>();
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("Wallet with id " + id + " does not exist");
        } else {
            Wallet foundWallet = wallet.get();
            foundWallet.setStatus(status);
            foundWallet = walletRepository.saveAndFlush(foundWallet);
            response.setMessage("Wallet status successfully updated");
            response.setStatus(HttpStatus.ACCEPTED);
            response.setData(foundWallet);
        }
        return response;
    }

    @Override
    public Response<Page<Wallet>> findWallet(String walletAcctNumber, Integer pageNumber, Integer pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("id").ignoreCase());
        pageNumber = (pageNumber == null || pageNumber == 0) ? 1 : pageNumber;
        pageSize = (pageSize == null || pageSize == 0) ? 10 : pageSize;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Wallet> wallets = walletRepository.findAllByWalletAccountNumberContaining(walletAcctNumber, pageable);
        Response<Page<Wallet>> response = new Response<>();
        response.setMessage("Wallets successfully retrieved");
        response.setStatus(HttpStatus.OK);
        response.setData(wallets);
        return response;
    }

    private String generateWalletAccountNumber() {
        int m = (int) Math.pow(10, 7);
        int randomNumber = m + new java.util.Random().nextInt(9 * m);
        return "00" + randomNumber;
    }
}
