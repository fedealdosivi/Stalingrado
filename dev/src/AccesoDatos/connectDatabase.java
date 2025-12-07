package AccesoDatos;

import Model.InformeBatalla;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class connectDatabase {

    private static final Logger logger = LoggerFactory.getLogger(connectDatabase.class);
    private static connectDatabase instance;
    private static final Properties config = new Properties();

    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String dbDriver;

    private connectDatabase() {
        loadConfiguration();
        this.dbUrl = config.getProperty("db.url");
        this.dbUsername = config.getProperty("db.username");
        this.dbPassword = config.getProperty("db.password");
        this.dbDriver = config.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(dbDriver);
            logger.info("Database driver loaded successfully: {}", dbDriver);
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load database driver: {}", dbDriver, e);
            throw new RuntimeException("Database driver not found", e);
        }
    }

    public static synchronized connectDatabase getInstance() {
        if (instance == null) {
            instance = new connectDatabase();
        }
        return instance;
    }

    private void loadConfiguration() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warn("application.properties not found, using defaults");
                setDefaultConfiguration();
                return;
            }
            config.load(input);
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration, using defaults", e);
            setDefaultConfiguration();
        }
    }

    private void setDefaultConfiguration() {
        config.setProperty("db.url", "jdbc:mysql://localhost:3306/batallas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        config.setProperty("db.username", "root");
        config.setProperty("db.password", "123456");
        config.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public void agregarInforme(InformeBatalla informe) {
        String query = "INSERT INTO informes (resultado) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, informe.getResultadoFinal());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Battle report saved to database successfully");
                System.out.println("Guardado en la base de datos\n");
            }
        } catch (SQLException e) {
            logger.error("Error saving battle report to database", e);
            System.err.println("Error al guardar en la base de datos: " + e.getMessage());
        }
    }

    public List<InformeBatalla> traerLista() {
        List<InformeBatalla> lista = new ArrayList<>();
        String query = "SELECT * FROM informes ORDER BY fecha_creacion DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                InformeBatalla informe = new InformeBatalla();
                informe.setId(rs.getInt("id"));
                informe.setResultadoFinal(rs.getString("resultado"));
                lista.add(informe);
            }

            logger.info("Retrieved {} battle reports from database", lista.size());
        } catch (SQLException e) {
            logger.error("Error retrieving battle reports from database", e);
            System.err.println("Error al obtener informes: " + e.getMessage());
        }

        return lista;
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            logger.info("Database connection test successful");
            return true;
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}
