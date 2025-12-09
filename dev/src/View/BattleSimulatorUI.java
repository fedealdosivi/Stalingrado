package View;

import Control.ControlBatallaUI;
import Model.InformeBatalla;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BattleSimulatorUI extends Application {

    private ControlBatallaUI controller;
    private Label statusLabel;
    private Label axisCountLabel;
    private Label urssCountLabel;
    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        controller = new ControlBatallaUI();

        primaryStage.setTitle("Stalingrado Battle Simulator");

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top - Title and Status
        VBox topBox = createTopSection();
        root.setTop(topBox);

        // Center - Main controls
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);

        // Bottom - Output area
        VBox bottomBox = createBottomSection();
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTopSection() {
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));
        topBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("STALINGRADO BATTLE SIMULATOR");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        statusLabel = new Label("Status: No inicializado");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");

        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);

        axisCountLabel = new Label("AXIS: 0 soldados");
        axisCountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        urssCountLabel = new Label("URSS: 0 soldados");
        urssCountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        statsBox.getChildren().addAll(axisCountLabel, urssCountLabel);

        Button initButton = new Button("Inicializar Ejercitos");
        initButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        initButton.setOnAction(e -> inicializarEjercitos());

        topBox.getChildren().addAll(titleLabel, statusLabel, statsBox, initButton);
        return topBox;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();

        // Tab 1: Add Units
        Tab addUnitsTab = new Tab("Agregar Unidades");
        addUnitsTab.setClosable(false);
        addUnitsTab.setContent(createAddUnitsPane());

        // Tab 2: Modify Bonuses
        Tab bonusesTab = new Tab("Bonificaciones");
        bonusesTab.setClosable(false);
        bonusesTab.setContent(createBonusesPane());

        // Tab 3: View Soldiers
        Tab viewSoldiersTab = new Tab("Ver Soldados");
        viewSoldiersTab.setClosable(false);
        viewSoldiersTab.setContent(createViewSoldiersPane());

        // Tab 4: Battle History
        Tab historyTab = new Tab("Historial de Batallas");
        historyTab.setClosable(false);
        historyTab.setContent(createHistoryPane());

        tabPane.getTabs().addAll(addUnitsTab, bonusesTab, viewSoldiersTab, historyTab);
        return tabPane;
    }

    private VBox createAddUnitsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Agregar Unidades al Ejercito");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Army selection
        HBox armyBox = new HBox(10);
        armyBox.setAlignment(Pos.CENTER_LEFT);
        Label armyLabel = new Label("Ejercito:");
        ComboBox<String> armyCombo = new ComboBox<>();
        armyCombo.getItems().addAll("AXIS", "URSS");
        armyCombo.setValue("AXIS");
        armyBox.getChildren().addAll(armyLabel, armyCombo);

        // Quantity input
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        Label quantityLabel = new Label("Cantidad:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner);

        // Unit type buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        Button avionesBtn = createUnitButton("Aviones", armyCombo, quantitySpinner, "avion");
        Button tanquesBtn = createUnitButton("Tanques", armyCombo, quantitySpinner, "tanque");
        Button cañonesBtn = createUnitButton("Cañones", armyCombo, quantitySpinner, "cañon");
        Button fusilerosBtn = createUnitButton("Fusileros", armyCombo, quantitySpinner, "fusilero");
        Button trincherosBtn = createUnitButton("Trincheros", armyCombo, quantitySpinner, "trinchero");
        Button cobardesBtn = createUnitButton("Cobardes", armyCombo, quantitySpinner, "cobarde");

        buttonGrid.add(avionesBtn, 0, 0);
        buttonGrid.add(tanquesBtn, 1, 0);
        buttonGrid.add(cañonesBtn, 2, 0);
        buttonGrid.add(fusilerosBtn, 0, 1);
        buttonGrid.add(trincherosBtn, 1, 1);
        buttonGrid.add(cobardesBtn, 2, 1);

        // Combat button
        Button combatBtn = new Button("INICIAR COMBATE");
        combatBtn.setStyle("-fx-font-size: 16px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10 20;");
        combatBtn.setOnAction(e -> iniciarCombate());

        pane.getChildren().addAll(titleLabel, armyBox, quantityBox, buttonGrid, combatBtn);
        return pane;
    }

    private Button createUnitButton(String name, ComboBox<String> armyCombo, Spinner<Integer> quantitySpinner, String unitType) {
        Button btn = new Button(name);
        btn.setPrefWidth(120);
        btn.setStyle("-fx-font-size: 12px;");
        btn.setOnAction(e -> {
            if (!controller.isInicializado()) {
                showError("Primero debe inicializar los ejercitos");
                return;
            }
            String army = armyCombo.getValue();
            int quantity = quantitySpinner.getValue();
            agregarUnidades(unitType, army, quantity);
        });
        return btn;
    }

    private VBox createBonusesPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Modificar Bonificaciones");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Army selection
        HBox armyBox = new HBox(10);
        armyBox.setAlignment(Pos.CENTER_LEFT);
        Label armyLabel = new Label("Ejercito:");
        ComboBox<String> armyCombo = new ComboBox<>();
        armyCombo.getItems().addAll("AXIS", "URSS");
        armyCombo.setValue("AXIS");
        armyBox.getChildren().addAll(armyLabel, armyCombo);

        // Attack bonus
        HBox attackBox = new HBox(10);
        attackBox.setAlignment(Pos.CENTER_LEFT);
        Label attackLabel = new Label("Bonif. Ataque:");
        TextField attackField = new TextField("1.0");
        Button attackBtn = new Button("Aplicar");
        attackBtn.setOnAction(e -> {
            if (!controller.isInicializado()) {
                showError("Primero debe inicializar los ejercitos");
                return;
            }
            try {
                double bonus = Double.parseDouble(attackField.getText());
                controller.cambiarBonificacionAtaque(armyCombo.getValue(), bonus);
                showSuccess("Bonificacion de ataque actualizada");
            } catch (NumberFormatException ex) {
                showError("Valor invalido");
            }
        });
        attackBox.getChildren().addAll(attackLabel, attackField, attackBtn);

        // Defense bonus
        HBox defenseBox = new HBox(10);
        defenseBox.setAlignment(Pos.CENTER_LEFT);
        Label defenseLabel = new Label("Bonif. Defensa:");
        TextField defenseField = new TextField("1.0");
        Button defenseBtn = new Button("Aplicar");
        defenseBtn.setOnAction(e -> {
            if (!controller.isInicializado()) {
                showError("Primero debe inicializar los ejercitos");
                return;
            }
            try {
                double bonus = Double.parseDouble(defenseField.getText());
                controller.cambiarBonificacionDefensa(armyCombo.getValue(), bonus);
                showSuccess("Bonificacion de defensa actualizada");
            } catch (NumberFormatException ex) {
                showError("Valor invalido");
            }
        });
        defenseBox.getChildren().addAll(defenseLabel, defenseField, defenseBtn);

        pane.getChildren().addAll(titleLabel, armyBox, attackBox, defenseBox);
        return pane;
    }

    private VBox createViewSoldiersPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Ver Soldados del Ejercito");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> armyCombo = new ComboBox<>();
        armyCombo.getItems().addAll("AXIS", "URSS");
        armyCombo.setValue("AXIS");

        Button viewBtn = new Button("Ver Soldados");
        viewBtn.setOnAction(e -> {
            if (!controller.isInicializado()) {
                showError("Primero debe inicializar los ejercitos");
                return;
            }
            mostrarSoldados(armyCombo.getValue());
        });

        controlBox.getChildren().addAll(armyCombo, viewBtn);

        TextArea soldiersArea = new TextArea();
        soldiersArea.setEditable(false);
        soldiersArea.setPrefRowCount(15);
        soldiersArea.setStyle("-fx-font-family: monospace;");

        pane.getChildren().addAll(titleLabel, controlBox, soldiersArea);

        viewBtn.setOnAction(e -> {
            if (!controller.isInicializado()) {
                showError("Primero debe inicializar los ejercitos");
                return;
            }
            String info = controller.obtenerSoldados(armyCombo.getValue());
            soldiersArea.setText(info);
        });

        return pane;
    }

    private VBox createHistoryPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label titleLabel = new Label("Historial de Batallas");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button refreshBtn = new Button("Actualizar Historial");
        TextArea historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefRowCount(15);
        historyArea.setStyle("-fx-font-family: monospace;");

        refreshBtn.setOnAction(e -> {
            ArrayList<InformeBatalla> batallas = controller.obtenerListaBatallas();
            StringBuilder sb = new StringBuilder();
            for (InformeBatalla informe : batallas) {
                sb.append(informe.toString()).append("\n\n");
            }
            historyArea.setText(sb.toString());
        });

        pane.getChildren().addAll(titleLabel, refreshBtn, historyArea);
        return pane;
    }

    private VBox createBottomSection() {
        VBox bottomBox = new VBox(5);
        bottomBox.setPadding(new Insets(10));

        Label outputLabel = new Label("Registro de Actividad:");
        outputLabel.setStyle("-fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(5);
        outputArea.setStyle("-fx-font-family: monospace; -fx-font-size: 11px;");

        bottomBox.getChildren().addAll(outputLabel, outputArea);
        return bottomBox;
    }

    private void inicializarEjercitos() {
        controller.inicializarEjercitos();
        statusLabel.setText("Status: Inicializado");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        updateStats();
        log("Ejercitos inicializados correctamente");
    }

    private void agregarUnidades(String tipo, String ejercito, int cantidad) {
        switch (tipo) {
            case "avion":
                controller.agregarAviones(cantidad, ejercito);
                break;
            case "tanque":
                controller.agregarTanques(cantidad, ejercito);
                break;
            case "cañon":
                controller.agregarCañoneros(cantidad, ejercito);
                break;
            case "fusilero":
                controller.agregarFusileros(cantidad, ejercito);
                break;
            case "trinchero":
                controller.agregarTrincheros(cantidad, ejercito);
                break;
            case "cobarde":
                controller.agregarCobardes(cantidad, ejercito);
                break;
        }
        updateStats();
        log("Agregados " + cantidad + " " + tipo + "(s) a " + ejercito);
    }

    private void iniciarCombate() {
        if (!controller.isInicializado()) {
            showError("Primero debe inicializar los ejercitos");
            return;
        }
        log("INICIANDO COMBATE...");
        controller.combatir();
        updateStats();
    }

    private void mostrarSoldados(String ejercito) {
        String info = controller.obtenerSoldados(ejercito);
        log("Mostrando soldados de " + ejercito);
    }

    private void updateStats() {
        int axisCount = controller.getCantidadSoldados("AXIS");
        int urssCount = controller.getCantidadSoldados("URSS");
        axisCountLabel.setText("AXIS: " + axisCount + " soldados");
        urssCountLabel.setText("URSS: " + urssCount + " soldados");
    }

    private void log(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        outputArea.appendText("[" + timestamp + "] " + message + "\n");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        log(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}