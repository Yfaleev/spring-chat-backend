package com.yfaleev.springchat.repository.api;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.yfaleev.springchat.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends EntityGraphJpaRepository<User, Long> {

    boolean existsByUsername(String userName);

    Optional<User> findByUsername(String userName);
}
