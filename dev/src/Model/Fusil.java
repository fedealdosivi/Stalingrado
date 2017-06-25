package Model;

import Interfaz.IAttack;

public class Fusil implements IAttack {


    @Override
    public int attack() {
        return 15;
    }

    @Override
    public String toString() {
        return "Fusil{}";
    }
}
