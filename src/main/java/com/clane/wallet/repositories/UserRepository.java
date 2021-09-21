package com.clane.wallet.repositories;

import com.clane.wallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhoneNumberOrEmailAddress(String phoneNumber, String emailAddress);
}
