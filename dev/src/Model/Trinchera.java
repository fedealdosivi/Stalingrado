package Model;

import Interfaz.IDefence;

public class Trinchera implements IDefence {


    @Override
    public int defence() {
        return 50;
    }

    @Override
    public String toString() {
        return "Trinchera{}";
    }
}
