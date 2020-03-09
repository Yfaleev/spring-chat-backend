package com.yfaleev.springchat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USR")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", unique = true, nullable = false, length = 30)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Message> messages;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
