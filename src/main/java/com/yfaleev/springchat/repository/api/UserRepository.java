package com.yfaleev.springchat.repository.api;

import com.yfaleev.springchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String userName);

    Optional<User> findByUsername(String userName);
}
