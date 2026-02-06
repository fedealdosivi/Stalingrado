package Web;

import Model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BattleWebService implements Observer {

    private Ejercito axis;
    private Ejercito urss;
    private CampoBatalla campo;
    private Consumer<String> messageCallback;
    private boolean battleInProgress = false;

    // In-memory battle history (no database needed for production)
    private static final List<InformeBatalla> battleHistory = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger battleIdCounter = new AtomicInteger(1);

    public BattleWebService() {
        // No database connection needed
    }

    public void setMessageCallback(Consumer<String> callback) {
        this.messageCallback = callback;
    }

    public void initialize() {
        this.axis = new Ejercito("AXIS");
        this.urss = new Ejercito("URSS");
        this.campo = new WebCampoBatalla(); // Use web-specific battlefield (no DB)

        axis.setCampo(campo);
        urss.setCampo(campo);
        campo.setEjercito1(axis);
        campo.setEjercito2(urss);
        campo.agregarObservador(this);

        battleInProgress = false;
    }

    public void addSoldiers(String army, String type, int count) {
        Ejercito ejercito = getEjercito(army);
        if (ejercito == null) return;

        for (int i = 0; i < count; i++) {
            switch (type.toLowerCase()) {
                case "tanque":
                    ejercito.agregarTanque();
                    break;
                case "avion":
                    ejercito.agregarAvion();
                    break;
                case "fusilero":
                    ejercito.agregarFusilero();
                    break;
                case "canon":
                case "cañon":
                    ejercito.agregarCañonero();
                    break;
                case "trinchero":
                    ejercito.agregarTrinchero();
                    break;
                case "cobarde":
                    ejercito.agregarCobarde();
                    break;
            }
        }
    }

    public void setBonus(String army, String type, double value) {
        Ejercito ejercito = getEjercito(army);
        if (ejercito == null) return;

        if ("attack".equalsIgnoreCase(type)) {
            ejercito.setBonifataque(value);
        } else if ("defense".equalsIgnoreCase(type)) {
            ejercito.setBonifdefensa(value);
        }
    }

    // Animation timing: move(1s) + clash(1.2s) + result(1.5s) + delay(0.5s) = ~4.2s
    private static final long COMBAT_ANIMATION_DELAY_MS = 4200;

    public void startBattle() {
        if (isInitialized() && !battleInProgress) {
            battleInProgress = true;

            // Set delay to allow web UI animations to complete between combats
            campo.setCombatDelayMs(COMBAT_ANIMATION_DELAY_MS);

            axis.start();
            urss.start();
        }
    }

    public int getSoldierCount(String army) {
        Ejercito ejercito = getEjercito(army);
        return ejercito != null ? ejercito.cantidadSoldados() : 0;
    }

    public double getAttackBonus(String army) {
        Ejercito ejercito = getEjercito(army);
        return ejercito != null ? ejercito.getBonifataque() : 0.0;
    }

    public double getDefenseBonus(String army) {
        Ejercito ejercito = getEjercito(army);
        return ejercito != null ? ejercito.getBonifdefensa() : 0.0;
    }

    public String getSoldiers(String army) {
        Ejercito ejercito = getEjercito(army);
        if (ejercito != null) {
            StringBuilder info = new StringBuilder();
            info.append("Ejercito: ").append(ejercito.getBando()).append("\n");
            info.append("Total soldados: ").append(ejercito.cantidadSoldados()).append("\n\n");
            for (Soldado s : ejercito.getListaSoldados()) {
                info.append(s.toString()).append("\n");
            }
            return info.toString();
        }
        return "Ejercito no encontrado";
    }

    public ArrayList<InformeBatalla> getBattleHistory() {
        // Return in-memory history (works without database)
        return new ArrayList<>(battleHistory);
    }

    public boolean isInitialized() {
        return axis != null && urss != null && campo != null;
    }

    public boolean isBattleInProgress() {
        return battleInProgress;
    }

    private Ejercito getEjercito(String army) {
        String armyUpper = army.toUpperCase();
        if ("AXIS".equals(armyUpper)) {
            return axis;
        } else if ("URSS".equals(armyUpper) || "USSR".equals(armyUpper)) {
            return urss;
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (messageCallback != null && arg != null) {
            String message = arg.toString();
            messageCallback.accept(message);

            // When battle ends, save to in-memory history
            if (message.contains("Termino la batalla")) {
                battleInProgress = false;

                // Save battle result to in-memory history
                InformeBatalla informe = new InformeBatalla();
                informe.setId(battleIdCounter.getAndIncrement());
                informe.setResultadoFinal(message);
                battleHistory.add(informe);
            }
        }
    }
}
