package AccesoDatos;

import Model.InformeBatalla;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Dao extends connectDatabase{


    private connectDatabase conn=connectDatabase.getInstance();

    public void agregarBatalla(InformeBatalla i)
    {
        this.conn.agregarInforme(i);
    }

    public ArrayList<InformeBatalla> traerLista()
    {
        return this.conn.traerLista();
    }
}
