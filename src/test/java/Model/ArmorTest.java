package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArmorTest {

    @Test
    void testChalecoDefense() {
        Chaleco chaleco = new Chaleco();
        assertEquals(30, chaleco.defence(), "Chaleco defense should be 30");
    }

    @Test
    void testTrincheraDefense() {
        Trinchera trinchera = new Trinchera();
        assertEquals(50, trinchera.defence(), "Trinchera defense should be 50");
    }

    @Test
    void testCorrerDefense() {
        Correr correr = new Correr();
        assertEquals(10, correr.defence(), "Correr defense should be 10");
    }

    @Test
    void testChalecoToString() {
        Chaleco chaleco = new Chaleco();
        assertEquals("Chaleco{}", chaleco.toString());
    }

    @Test
    void testTrincheraToString() {
        Trinchera trinchera = new Trinchera();
        assertEquals("Trinchera{}", trinchera.toString());
    }

    @Test
    void testCorrerToString() {
        Correr correr = new Correr();
        assertEquals("Correr{}", correr.toString());
    }

    @Test
    void testDefenseRanking() {
        Correr correr = new Correr();
        Chaleco chaleco = new Chaleco();
        Trinchera trinchera = new Trinchera();

        assertTrue(correr.defence() < chaleco.defence(), "Correr should be weaker than Chaleco");
        assertTrue(chaleco.defence() < trinchera.defence(), "Chaleco should be weaker than Trinchera");
    }
}
