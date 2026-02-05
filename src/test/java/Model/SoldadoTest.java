package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SoldadoTest {

    private Soldado soldado;

    @BeforeEach
    void setUp() {
        soldado = new Soldado();
    }

    @Test
    void testSoldadoWithRifle() {
        soldado.usarFusil();
        assertEquals(15, soldado.attack(), "Rifle should have attack power of 15");
        assertEquals(30, soldado.defence(), "Default vest should have defense of 30");
    }

    @Test
    void testSoldadoWithTank() {
        soldado.subirseAlTanque();
        assertEquals(40, soldado.attack(), "Tank should have attack power of 40");
        assertEquals(30, soldado.defence(), "Default vest should have defense of 30");
    }

    @Test
    void testSoldadoWithAircraft() {
        soldado.subirseAlAvion();
        assertEquals(50, soldado.attack(), "Aircraft should have attack power of 50");
        assertEquals(30, soldado.defence(), "Default vest should have defense of 30");
    }

    @Test
    void testSoldadoWithCannon() {
        soldado.usarCañon();
        assertEquals(25, soldado.attack(), "Cannon should have attack power of 25");
        assertEquals(30, soldado.defence(), "Default vest should have defense of 30");
    }

    @Test
    void testSoldadoInTrench() {
        soldado.usarTrinchera();
        assertEquals(15, soldado.attack(), "Rifle should have attack power of 15");
        assertEquals(50, soldado.defence(), "Trench should have defense of 50");
    }

    @Test
    void testCowardSoldado() {
        soldado.escaparse();
        assertEquals(15, soldado.attack(), "Rifle should have attack power of 15");
        assertEquals(10, soldado.defence(), "Fleeing should have defense of 10");
    }

    @Test
    void testSoldadoHasUniqueId() {
        Soldado soldado1 = new Soldado();
        Soldado soldado2 = new Soldado();
        assertNotEquals(soldado1.getId(), soldado2.getId(), "Each soldier should have a unique ID");
    }

    @Test
    void testDefaultEquipment() {
        Soldado newSoldado = new Soldado();
        assertEquals(15, newSoldado.attack(), "Default attack should be Fusil (15)");
        assertEquals(30, newSoldado.defence(), "Default defense should be Chaleco (30)");
    }

    @Test
    void testEquipmentChange() {
        soldado.subirseAlTanque();
        soldado.usarTrinchera();
        assertEquals(40, soldado.attack(), "Attack should be tank (40)");
        assertEquals(50, soldado.defence(), "Defense should be trench (50)");

        soldado.usarFusil();
        soldado.ponerseChaleco();
        assertEquals(15, soldado.attack(), "Attack should be back to rifle (15)");
        assertEquals(30, soldado.defence(), "Defense should be back to vest (30)");
    }

    @Test
    void testToString() {
        String str = soldado.toString();
        assertTrue(str.contains("Soldado"), "toString should contain Soldado");
        assertTrue(str.contains("id="), "toString should contain id");
    }

    @Test
    void testGettersAndSetters() {
        soldado.setId(999L);
        assertEquals(999L, soldado.getId(), "getId should return set id");

        assertNotNull(soldado.getAtaque(), "getAtaque should not be null");
        assertNotNull(soldado.getDefenza(), "getDefenza should not be null");
    }
}
