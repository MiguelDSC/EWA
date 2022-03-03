package app.rest.greenhouse;

import app.repositories.greenhouse.GreenhouseData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@EnableScheduling
@Component
public class GreenhouseHandler extends TextWebSocketHandler {

    @Qualifier("greenhouseDataJpa")
    @Autowired
    private GreenhouseData greenhouseData;

    //TODO: replace session list with sessionMap.
    private Map<Integer, Set<WebSocketSession>> sessionMap = new HashMap<>();

    private List<WebSocketSession> session = new CopyOnWriteArrayList<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("Hello"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.session.remove(session);
    }

    /**
     * Scheduled task for websocket.
     */
    @Scheduled(fixedRate = 5000)
    public void update() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();

        // Send data to websockets.
        for (WebSocketSession socket: this.session) {
            try {
                String s = objectMapper.writeValueAsString(this.greenhouseData.getLast(1));
                socket.sendMessage(new TextMessage(s));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
