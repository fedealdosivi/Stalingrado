package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EjercitoTest {

    private Ejercito ussr;
    private Ejercito axis;

    @BeforeEach
    void setUp() {
        ussr = new Ejercito("USSR");
        axis = new Ejercito("AXIS");
    }

    @Test
    void testUSSRBonifications() {
        assertEquals(0.95, ussr.getBonifataque(), 0.001, "USSR attack bonus should be 0.95");
        assertEquals(1.95, ussr.getBonifdefensa(), 0.001, "USSR defense bonus should be 1.95");
    }

    @Test
    void testAXISBonifications() {
        assertEquals(2.95, axis.getBonifataque(), 0.001, "AXIS attack bonus should be 2.95");
        assertEquals(1.00, axis.getBonifdefensa(), 0.001, "AXIS defense bonus should be 1.00");
    }

    @Test
    void testAddSoldier() {
        ussr.agregarFusilero();
        assertEquals(1, ussr.cantidadSoldados(), "Should have 1 soldier after adding");
        assertTrue(ussr.haySoldados(), "Army should have soldiers");
    }

    @Test
    void testAddMultipleSoldiers() {
        ussr.agregarFusilero();
        ussr.agregarTanque();
        ussr.agregarAvion();
        assertEquals(3, ussr.cantidadSoldados(), "Should have 3 soldiers");
    }

    @Test
    void testCalculateAttackWithBonus() {
        Soldado soldado = new Soldado();
        soldado.usarFusil(); // Attack power 15

        int axisAttack = axis.calcularAtaque(soldado);
        assertEquals(44, axisAttack, "AXIS attack: 15 * 2.95 = 44");

        int ussrAttack = ussr.calcularAtaque(soldado);
        assertEquals(14, ussrAttack, "USSR attack: 15 * 0.95 = 14");
    }

    @Test
    void testCalculateDefenseWithBonus() {
        Soldado soldado = new Soldado();
        soldado.usarFusil(); // Defense power 30 (vest)

        int ussrDefense = ussr.calcularDefensa(soldado);
        assertEquals(58, ussrDefense, "USSR defense: 30 * 1.95 = 58");

        int axisDefense = axis.calcularDefensa(soldado);
        assertEquals(30, axisDefense, "AXIS defense: 30 * 1.00 = 30");
    }

    @Test
    void testRemoveSoldier() {
        ussr.agregarFusilero();
        Soldado soldado = ussr.getListaSoldados().get(0);
        ussr.eliminarSoldado(soldado);
        assertEquals(0, ussr.cantidadSoldados(), "Should have 0 soldiers after removal");
        assertFalse(ussr.haySoldados(), "Army should not have soldiers");
    }

    @Test
    void testRandomSoldierSelection() {
        ussr.agregarFusilero();
        ussr.agregarTanque();
        ussr.agregarAvion();

        Soldado randomSoldado = ussr.randomSoldado();
        assertNotNull(randomSoldado, "Should return a random soldier");
    }

    @Test
    void testEjercitoHasUniqueBando() {
        assertEquals("USSR", ussr.getBando(), "USSR army should have USSR bando");
        assertEquals("AXIS", axis.getBando(), "AXIS army should have AXIS bando");
    }
}
