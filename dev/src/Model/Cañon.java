package Model;

import Interfaz.IAttack;

public class Cañon implements IAttack{


    @Override
    public int attack() {
        return 25;
    }

    @Override
    public String toString() {
        return "Cañon{}";
    }
}
