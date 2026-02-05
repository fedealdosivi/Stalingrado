package Web;

import Model.CampoBatalla;
import Model.InformeBatalla;

/**
 * Web-specific CampoBatalla that doesn't save to database.
 * Used for production deployment without MySQL dependency.
 */
public class WebCampoBatalla extends CampoBatalla {

    @Override
    public synchronized boolean todaviaHaySoldados() {
        boolean termino = false;
        if (getEjercito1().cantidadSoldados() <= 0 || getEjercito2().cantidadSoldados() <= 0) {
            termino = true;
            notifyAll();
            String mensaje = "Termino la batalla\nSoldados vivos:\n " + 
                "Axis: " + getEjercito1().cantidadSoldados() + 
                " Urss: " + getEjercito2().cantidadSoldados();
            setChanged();
            notifyObservers(mensaje);
            // Skip database save - history is handled in-memory by BattleWebService
        }
        return termino;
    }
}
