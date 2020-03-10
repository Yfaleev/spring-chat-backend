package com.yfaleev.springchat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = Message.GRAPH_USER,
        attributeNodes = {
                @NamedAttributeNode(value = "user"),
        }
)
@Entity
@Table(name = "MESSAGE")
@Getter
@Setter
@NoArgsConstructor
public class Message {

    public static final String GRAPH_USER = "Message.user";

    @Id
    @Column(name = "MESSAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "MESSAGE_TEXT", nullable = false)
    private String text;

    @Column(name = "SEND_DATE", nullable = false, updatable = false)
    private LocalDateTime sendDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    public Message(String text) {
        this.text = text;
    }
}
