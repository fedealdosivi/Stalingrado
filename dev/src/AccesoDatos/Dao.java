package AccesoDatos;

import Model.InformeBatalla;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Dao {

    private connectDatabase conn = connectDatabase.getInstance();

    public void agregarBatalla(InformeBatalla i) {
        this.conn.agregarInforme(i);
    }

    public ArrayList<InformeBatalla> traerLista() {
        return new ArrayList<InformeBatalla>(this.conn.traerLista());
    }
}
