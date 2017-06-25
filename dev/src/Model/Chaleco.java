package Model;

import Interfaz.IDefence;

public class Chaleco implements IDefence {


    @Override
    public int defence() {
        return 30;
    }

    @Override
    public String toString() {
        return "Chaleco{}";
    }
}
