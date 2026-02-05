package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Observer;

class CampoBatallaTest {

    private CampoBatalla campoBatalla;
    private Ejercito ussr;
    private Ejercito axis;

    @BeforeEach
    void setUp() {
        campoBatalla = new CampoBatalla();
        ussr = new Ejercito("USSR");
        axis = new Ejercito("AXIS");
        campoBatalla.setEjercito1(axis);
        campoBatalla.setEjercito2(ussr);
    }

    @Test
    void testInitialState() {
        assertTrue(campoBatalla.isDisponible(), "Battlefield should be available initially");
        assertFalse(campoBatalla.isGanador(), "No winner initially");
    }

    @Test
    void testSetEjercitos() {
        assertEquals(axis, campoBatalla.getEjercito1(), "Ejercito1 should be AXIS");
        assertEquals(ussr, campoBatalla.getEjercito2(), "Ejercito2 should be USSR");
    }

    @Test
    void testSetDisponible() {
        campoBatalla.setDisponible(false);
        assertFalse(campoBatalla.isDisponible(), "Should be unavailable after setting");
        
        campoBatalla.setDisponible(true);
        assertTrue(campoBatalla.isDisponible(), "Should be available after setting");
    }

    @Test
    void testSetGanador() {
        campoBatalla.setGanador(true);
        assertTrue(campoBatalla.isGanador(), "Should have winner after setting");
        
        campoBatalla.setGanador(false);
        assertFalse(campoBatalla.isGanador(), "Should not have winner after resetting");
    }

    @Test
    void testTodaviaHaySoldadosWithEmptyArmies() {
        // Both armies empty - battle should end
        assertTrue(campoBatalla.todaviaHaySoldados(), "Battle should end when armies are empty");
    }

    @Test
    void testTodaviaHaySoldadosWithSoldiers() {
        ussr.agregarFusilero();
        axis.agregarFusilero();
        
        assertFalse(campoBatalla.todaviaHaySoldados(), "Battle should continue when both armies have soldiers");
    }

    @Test
    void testTodaviaHaySoldadosOneArmyEmpty() {
        ussr.agregarFusilero();
        // axis is empty
        
        assertTrue(campoBatalla.todaviaHaySoldados(), "Battle should end when one army is empty");
    }

    @Test
    void testAddObserver() {
        final boolean[] notified = {false};
        Observer observer = (o, arg) -> notified[0] = true;

        campoBatalla.agregarObservador(observer);
        // Trigger observer via todaviaHaySoldados (which calls setChanged internally)
        campoBatalla.todaviaHaySoldados();

        assertTrue(notified[0], "Observer should be notified");
    }
}
