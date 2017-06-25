package Model;

import Interfaz.IDefence;

public class Correr implements IDefence {

    @Override
    public int defence() {
        return 10;
    }

    @Override
    public String toString() {
        return "Correr{}";
    }
}
