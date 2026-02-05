package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeaponTest {

    @Test
    void testFusilAttack() {
        Fusil fusil = new Fusil();
        assertEquals(15, fusil.attack(), "Fusil attack should be 15");
    }

    @Test
    void testTanqueAttack() {
        Tanque tanque = new Tanque();
        assertEquals(40, tanque.attack(), "Tanque attack should be 40");
    }

    @Test
    void testAvionCombateAttack() {
        AvionCombate avion = new AvionCombate();
        assertEquals(50, avion.attack(), "AvionCombate attack should be 50");
    }

    @Test
    void testCañonAttack() {
        Cañon cañon = new Cañon();
        assertEquals(25, cañon.attack(), "Cañon attack should be 25");
    }

    @Test
    void testFusilToString() {
        Fusil fusil = new Fusil();
        assertEquals("Fusil{}", fusil.toString());
    }

    @Test
    void testTanqueToString() {
        Tanque tanque = new Tanque();
        assertEquals("Tanque{}", tanque.toString());
    }

    @Test
    void testAvionCombateToString() {
        AvionCombate avion = new AvionCombate();
        assertEquals("AvionCombate{}", avion.toString());
    }

    @Test
    void testCañonToString() {
        Cañon cañon = new Cañon();
        assertEquals("Cañon{}", cañon.toString());
    }

    @Test
    void testWeaponPowerRanking() {
        Fusil fusil = new Fusil();
        Cañon cañon = new Cañon();
        Tanque tanque = new Tanque();
        AvionCombate avion = new AvionCombate();

        assertTrue(fusil.attack() < cañon.attack(), "Fusil should be weaker than Cañon");
        assertTrue(cañon.attack() < tanque.attack(), "Cañon should be weaker than Tanque");
        assertTrue(tanque.attack() < avion.attack(), "Tanque should be weaker than AvionCombate");
    }
}
