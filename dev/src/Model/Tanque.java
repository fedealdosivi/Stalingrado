package Model;

import Interfaz.IAttack;
import Interfaz.IDefence;

public class Tanque  implements IAttack{



    @Override
    public int attack() {
        return 40;
    }

    @Override
    public String toString() {
        return "Tanque{}";
    }
}
