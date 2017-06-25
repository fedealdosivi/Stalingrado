package Model;

import Interfaz.IAttack;

public class AvionCombate implements IAttack{


    @Override
    public int attack() {
        return 50;
    }


    @Override
    public String toString() {
        return "AvionCombate{}";
    }
}
