package Web;

import Model.InformeBatalla;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BattleWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, BattleWebService> sessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        BattleWebService service = new BattleWebService();
        service.setMessageCallback(message -> {
            try {
                sendCombatEvent(session, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sessions.put(session.getId(), service);
        
        try {
            sendMessage(session, "connected", Map.of("sessionId", session.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        BattleWebService service = sessions.get(session.getId());
        if (service == null) return;

        JsonObject json = JsonParser.parseString(message.getPayload()).getAsJsonObject();
        String action = json.get("action").getAsString();

        switch (action) {
            case "init":
                handleInit(session, service);
                break;
            case "addSoldiers":
                handleAddSoldiers(session, service, json);
                break;
            case "setBonus":
                handleSetBonus(session, service, json);
                break;
            case "start":
                handleStart(session, service);
                break;
            case "getStatus":
                handleGetStatus(session, service);
                break;
            case "getSoldiers":
                handleGetSoldiers(session, service, json);
                break;
            case "getHistory":
                handleGetHistory(session, service);
                break;
            default:
                sendError(session, "Unknown action: " + action);
        }
    }

    private void handleInit(WebSocketSession session, BattleWebService service) throws IOException {
        service.initialize();
        sendMessage(session, "initialized", Map.of(
            "axis", Map.of(
                "soldiers", service.getSoldierCount("AXIS"),
                "attackBonus", service.getAttackBonus("AXIS"),
                "defenseBonus", service.getDefenseBonus("AXIS")
            ),
            "urss", Map.of(
                "soldiers", service.getSoldierCount("URSS"),
                "attackBonus", service.getAttackBonus("URSS"),
                "defenseBonus", service.getDefenseBonus("URSS")
            )
        ));
    }

    private void handleAddSoldiers(WebSocketSession session, BattleWebService service, JsonObject json) throws IOException {
        String army = json.get("army").getAsString();
        String type = json.get("type").getAsString();
        int count = json.get("count").getAsInt();
        
        service.addSoldiers(army, type, count);
        
        sendMessage(session, "soldiersAdded", Map.of(
            "army", army,
            "soldierType", type,
            "count", service.getSoldierCount(army)
        ));
    }

    private void handleSetBonus(WebSocketSession session, BattleWebService service, JsonObject json) throws IOException {
        String army = json.get("army").getAsString();
        String type = json.get("type").getAsString();
        double value = json.get("value").getAsDouble();
        
        service.setBonus(army, type, value);
        
        sendMessage(session, "bonusSet", Map.of(
            "army", army,
            "type", type,
            "attackBonus", service.getAttackBonus(army),
            "defenseBonus", service.getDefenseBonus(army)
        ));
    }

    private void handleStart(WebSocketSession session, BattleWebService service) throws IOException {
        if (!service.isInitialized()) {
            sendError(session, "Battle not initialized. Call init first.");
            return;
        }
        if (service.isBattleInProgress()) {
            sendError(session, "Battle already in progress.");
            return;
        }
        if (service.getSoldierCount("AXIS") == 0 || service.getSoldierCount("URSS") == 0) {
            sendError(session, "Both armies need soldiers to start battle.");
            return;
        }
        
        service.startBattle();
        sendMessage(session, "battleStarted", Map.of(
            "axis", service.getSoldierCount("AXIS"),
            "urss", service.getSoldierCount("URSS")
        ));
    }

    private void handleGetStatus(WebSocketSession session, BattleWebService service) throws IOException {
        sendMessage(session, "status", Map.of(
            "initialized", service.isInitialized(),
            "battleInProgress", service.isBattleInProgress(),
            "axis", Map.of(
                "soldiers", service.getSoldierCount("AXIS"),
                "attackBonus", service.getAttackBonus("AXIS"),
                "defenseBonus", service.getDefenseBonus("AXIS")
            ),
            "urss", Map.of(
                "soldiers", service.getSoldierCount("URSS"),
                "attackBonus", service.getAttackBonus("URSS"),
                "defenseBonus", service.getDefenseBonus("URSS")
            )
        ));
    }

    private void handleGetSoldiers(WebSocketSession session, BattleWebService service, JsonObject json) throws IOException {
        String army = json.get("army").getAsString();
        String soldiers = service.getSoldiers(army);
        sendMessage(session, "soldiers", Map.of(
            "army", army,
            "list", soldiers
        ));
    }

    private void handleGetHistory(WebSocketSession session, BattleWebService service) throws IOException {
        ArrayList<InformeBatalla> history = service.getBattleHistory();
        ArrayList<Map<String, Object>> historyList = new ArrayList<>();
        
        if (history != null) {
            for (InformeBatalla batalla : history) {
                historyList.add(Map.of(
                    "id", batalla.getId(),
                    "resultado", batalla.getResultadoFinal() != null ? batalla.getResultadoFinal() : ""
                ));
            }
        }
        
        sendMessage(session, "history", Map.of("battles", historyList));
    }

    private void sendCombatEvent(WebSocketSession session, String message) throws IOException {
        if (session.isOpen()) {
            sendMessage(session, "combatEvent", Map.of("message", message));
        }
    }

    private void sendMessage(WebSocketSession session, String type, Map<String, Object> data) throws IOException {
        Map<String, Object> response = new java.util.HashMap<>(data);
        response.put("type", type);
        session.sendMessage(new TextMessage(gson.toJson(response)));
    }

    private void sendError(WebSocketSession session, String error) throws IOException {
        sendMessage(session, "error", Map.of("message", error));
    }
}
