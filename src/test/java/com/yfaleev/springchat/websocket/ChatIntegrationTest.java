package com.yfaleev.springchat.websocket;

import com.yfaleev.springchat.dto.ChatMessageDto;
import com.yfaleev.springchat.dto.ChatMessageHistoryDto;
import com.yfaleev.springchat.dto.ChatUsersNamesDto;
import com.yfaleev.springchat.format.datetime.api.LocalDateTimeFormatter;
import com.yfaleev.springchat.model.Message;
import com.yfaleev.springchat.model.User;
import com.yfaleev.springchat.model.notEntityModel.UserPrincipal;
import com.yfaleev.springchat.service.api.AuthenticationService;
import com.yfaleev.springchat.service.api.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatIntegrationTest {

    public static final String ACTIVE_USERS_DESTINATION = "/app/chat.activeUsers";
    public static final String MESSAGE_HISTORY_DESTINATION = "/app/chat.messageHistory";
    public static final String CHAT_DESTINATION = "/app/chat.sendMessage";

    public static final String CHAT_BROKER = "/topic/public";

    private String handshakeUrl;

    @Value("${local.server.port}")
    private int port;

    private BlockingQueue<Object> blockingQueue;

    private WebSocketStompClient stompClient;

    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private LocalDateTimeFormatter dateTimeFormatter;
    @MockBean
    private SimpUserRegistry simpUserRegistry;

    private String username = "username";
    private String password = "password";
    private UsernamePasswordAuthenticationToken token;

    @BeforeEach
    public void setUp() {
        handshakeUrl = "ws://localhost:" + port + "/ws";

        blockingQueue = new LinkedBlockingDeque<>();

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        UserDetails userDetails = UserPrincipal
                .builder()
                .id(1L)
                .authorities(Collections.emptyList())
                .password(password)
                .username(username)
                .build();

        token = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Test
    public void whenAttemptConnectionWithInvalidCredentials_ThenFail() throws Exception {
        when(authenticationService.getAuthenticationToken(anyString(), anyString())).thenThrow(BadCredentialsException.class);

        try {
            connectWithAuthHeaders();
            fail();
        } catch (ExecutionException e) {
            assertThat(e).hasCauseExactlyInstanceOf(ConnectionLostException.class);
        }
    }

    @Test
    public void whenAttemptConnectionWithCorrectCredentials_ThenSuccessConnection() throws Exception {
        when(authenticationService.getAuthenticationToken(username, password)).thenReturn(token);

        StompSession stompSession = connectWithAuthHeaders();

        assertTrue(stompSession.isConnected());
    }

    @Test
    public void whenMessageSendInChat_ThenReceiveCorrectMessage() throws Exception {
        ChatMessageDto messageDto = new ChatMessageDto("hello");

        LocalDateTime now = LocalDateTime.now();

        Message message = new Message() {{
            setId(1L);
            setText(messageDto.getMessageText());
            setSendDate(now);
            setUser(new User() {{
                setId(1L);
                setUsername(username);
                setPassword(password);
            }});
        }};

        when(messageService.save(any(Message.class), any(UserPrincipal.class))).thenReturn(message);
        when(authenticationService.getAuthenticationToken(username, password)).thenReturn(token);

        String dateAsString = "now";
        when(dateTimeFormatter.format(now)).thenReturn(dateAsString);

        StompSession stompSession = connectWithAuthHeaders();
        stompSession.subscribe(CHAT_BROKER, new BlockingQueueOfferingFrameHandler<>(ChatMessageDto.class));

        stompSession.send(CHAT_DESTINATION, messageDto);

        ChatMessageDto receivedMessageDto = pollAndAssertInstance(ChatMessageDto.class);

        assertNotNull(receivedMessageDto);
        assertEquals(messageDto.getMessageText(), receivedMessageDto.getMessageText());
        assertEquals(ChatMessageDto.ChatMessageType.CHATTING, receivedMessageDto.getMessageType());
        assertEquals(dateAsString, receivedMessageDto.getSendDate());
        assertEquals(username, receivedMessageDto.getSender());
    }

    @Test
    public void whenSubscribedToShowActiveUsers_ThenReceiveUserNames() throws Exception {
        String username1 = "username1";
        String username2 = "username2";

        SimpUser mockUser1 = Mockito.mock(SimpUser.class);
        SimpUser mockUser2 = Mockito.mock(SimpUser.class);
        when(mockUser1.getName()).thenReturn(username1);
        when(mockUser2.getName()).thenReturn(username2);

        Set<SimpUser> simpUserMocks = new HashSet<>();
        simpUserMocks.add(mockUser1);
        simpUserMocks.add(mockUser2);

        when(authenticationService.getAuthenticationToken(username, password)).thenReturn(token);
        when(simpUserRegistry.getUsers()).thenReturn(simpUserMocks);

        StompSession stompSession = connectWithAuthHeaders();
        stompSession.subscribe(ACTIVE_USERS_DESTINATION, new BlockingQueueOfferingFrameHandler<>(ChatUsersNamesDto.class));

        ChatUsersNamesDto chatUsersNamesDto = pollAndAssertInstance(ChatUsersNamesDto.class);

        assertNotNull(chatUsersNamesDto);
        List<String> userNames = chatUsersNamesDto.getUserNames();
        assertNotNull(userNames);
        assertThat(userNames).hasSize(2);
        assertTrue(userNames.stream().anyMatch(username1::equals));
        assertTrue(userNames.stream().anyMatch(username2::equals));
    }

    @Test
    public void whenSubscribedToShowMessageHistory_ThenReceiveMessageList() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Message message1 = new Message() {{
            setId(1L);
            setText("text1");
            setSendDate(now);
            setUser(new User() {{
                setId(1L);
                setUsername("username1");
                setPassword("password1");
            }});
        }};
        Message message2 = new Message() {{
            setId(1L);
            setText("text2");
            setSendDate(now);
            setUser(new User() {{
                setId(1L);
                setUsername("username2");
                setPassword("password2");
            }});
        }};

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);

        when(authenticationService.getAuthenticationToken(username, password)).thenReturn(token);
        when(messageService.findAllWithUsers()).thenReturn(messages);

        String dateAsString = "now";
        when(dateTimeFormatter.format(now)).thenReturn(dateAsString);

        StompSession stompSession = connectWithAuthHeaders();
        stompSession.subscribe(MESSAGE_HISTORY_DESTINATION, new BlockingQueueOfferingFrameHandler<>(ChatMessageHistoryDto.class));

        ChatMessageHistoryDto chatMessageHistoryDto = pollAndAssertInstance(ChatMessageHistoryDto.class);

        assertNotNull(chatMessageHistoryDto);
        List<ChatMessageDto> chatMessages = chatMessageHistoryDto.getChatMessages();

        assertNotNull(chatMessages);
        assertThat(chatMessages).hasSize(2);

        assertTrue(chatMessages.stream().allMatch(msg -> ChatMessageDto.ChatMessageType.CHATTING == msg.getMessageType()));

        chatMessages.forEach(msg -> assertEquals(dateAsString, msg.getSendDate()));

        assertTrue(chatMessages.stream().anyMatch(msg -> message1.getText().equals(msg.getMessageText())));
        assertTrue(chatMessages.stream().anyMatch(msg -> message2.getText().equals(msg.getMessageText())));

        assertTrue(chatMessages.stream().anyMatch(msg -> message1.getUser().getUsername().equals(msg.getSender())));
        assertTrue(chatMessages.stream().anyMatch(msg -> message2.getUser().getUsername().equals(msg.getSender())));
    }

    private <T> T pollAndAssertInstance(Class<T> tClass) throws InterruptedException {
        Object obj = blockingQueue.poll(1, SECONDS);
        assertThat(obj).isInstanceOf(tClass);
        return (T) obj;
    }

    private StompSession connectWithAuthHeaders() throws InterruptedException, ExecutionException, TimeoutException {
        StompHeaders authHeaders = buildAuthHeaders(username, password);

        return stompClient
                .connect(
                        handshakeUrl,
                        new WebSocketHttpHeaders(),
                        authHeaders,
                        new StompSessionHandlerAdapter() {
                        }
                ).get(1, SECONDS);
    }

    private StompHeaders buildAuthHeaders(String username, String password) {
        StompHeaders authHeaders = new StompHeaders();
        authHeaders.add("login", username);
        authHeaders.add("password", password);
        return authHeaders;
    }

    class BlockingQueueOfferingFrameHandler<PAYLOAD_TYPE> implements StompFrameHandler {
        Class<PAYLOAD_TYPE> payloadTypeClass;

        BlockingQueueOfferingFrameHandler(Class<PAYLOAD_TYPE> payloadTypeClass) {
            this.payloadTypeClass = payloadTypeClass;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return payloadTypeClass;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(o);
        }
    }
}
