package com.yfaleev.springchat.repository.api;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.yfaleev.springchat.model.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends EntityGraphJpaRepository<Message, Long> {
}
