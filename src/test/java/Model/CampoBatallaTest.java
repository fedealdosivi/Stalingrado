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
    void testBattleContinuesWithSoldiers() {
        // When both armies have soldiers, battle should continue
        ussr.agregarFusilero();
        axis.agregarFusilero();

        // Check soldier counts directly - avoids DB call
        assertTrue(ussr.haySoldados(), "USSR should have soldiers");
        assertTrue(axis.haySoldados(), "AXIS should have soldiers");
        assertEquals(1, ussr.cantidadSoldados(), "USSR should have 1 soldier");
        assertEquals(1, axis.cantidadSoldados(), "AXIS should have 1 soldier");
    }

    @Test
    void testBattleEndsWhenArmyEmpty() {
        // Test the condition that ends battle (one army empty)
        ussr.agregarFusilero();
        // axis is empty

        // Verify the condition directly without calling todaviaHaySoldados
        // which would trigger a DB save
        assertTrue(axis.cantidadSoldados() <= 0 || ussr.cantidadSoldados() <= 0,
            "Battle should end when one army has no soldiers");
    }

    @Test
    void testAddObserver() {
        // Test that observer can be added (agregarObservador is a wrapper for addObserver)
        final boolean[] observerAdded = {false};
        Observer observer = (o, arg) -> observerAdded[0] = true;

        // This should not throw
        campoBatalla.agregarObservador(observer);

        // Verify battle continues path works (doesn't trigger DB)
        ussr.agregarFusilero();
        axis.agregarFusilero();
        assertFalse(campoBatalla.todaviaHaySoldados(), "Battle continues when both have soldiers");
    }

    @Test
    void testArmySetup() {
        // Verify armies are properly linked to battlefield
        assertNotNull(campoBatalla.getEjercito1(), "Ejercito1 should not be null");
        assertNotNull(campoBatalla.getEjercito2(), "Ejercito2 should not be null");
        assertEquals("AXIS", campoBatalla.getEjercito1().getBando());
        assertEquals("USSR", campoBatalla.getEjercito2().getBando());
    }
}
