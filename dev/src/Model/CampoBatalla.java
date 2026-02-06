package Model;

import AccesoDatos.Dao;

import java.util.Observable;
import java.util.Observer;

public class CampoBatalla extends Observable {

    private boolean disponible = true;
    private Ejercito ejercito1;
    private Ejercito ejercito2;
    private boolean ganador = false;

    // Optional delay between combats (in milliseconds)
    // Default is 0 (no delay) for console/JavaFX
    // Web UI sets this to allow time for animations
    private long combatDelayMs = 0;

    public void agregarObservador(Observer observer)
    {
        addObserver(observer);
    }

    public synchronized void combate(String bando) {

        try{
            if (this.isDisponible() && !this.isGanador()) {
                this.setDisponible(false);

                if (ejercito1.getBando().equals(bando)) {
                    String msg="Entro " + ejercito1.getBando();
                    msg=msg+" "+ejercito1.enfrentarse(ejercito2);
                    // Append soldier counts for web UI parsing
                    msg=msg+" Soldados vivos: Axis: "+ejercito1.cantidadSoldados()+" Urss: "+ejercito2.cantidadSoldados();
                    this.setDisponible(true);
                    notifyAll();
                    setChanged();
                    notifyObservers(msg);
                    this.ganador = todaviaHaySoldados();

                    // Wait for UI animation if delay is configured
                    if (combatDelayMs > 0 && !this.ganador) {
                        System.out.println("[CampoBatalla] Applying delay: " + combatDelayMs + "ms");
                        Thread.sleep(combatDelayMs);
                        System.out.println("[CampoBatalla] Delay completed");
                    } else {
                        System.out.println("[CampoBatalla] No delay: delayMs=" + combatDelayMs + ", ganador=" + this.ganador);
                    }
                }

                if (ejercito2.getBando().equals(bando)) {
                    String msg="Entro " + ejercito2.getBando();
                    msg=msg+" "+ejercito2.enfrentarse(ejercito1);
                    // Append soldier counts for web UI parsing
                    msg=msg+" Soldados vivos: Axis: "+ejercito1.cantidadSoldados()+" Urss: "+ejercito2.cantidadSoldados();
                    this.setDisponible(true);
                    notifyAll();
                    setChanged();
                    notifyObservers(msg);
                    this.ganador = todaviaHaySoldados();

                    // Wait for UI animation if delay is configured
                    if (combatDelayMs > 0 && !this.ganador) {
                        System.out.println("[CampoBatalla] Applying delay: " + combatDelayMs + "ms");
                        Thread.sleep(combatDelayMs);
                        System.out.println("[CampoBatalla] Delay completed");
                    } else {
                        System.out.println("[CampoBatalla] No delay: delayMs=" + combatDelayMs + ", ganador=" + this.ganador);
                    }
                }
            } else {
                wait();
            }
        }
        catch (InterruptedException e)
        {
            e.getStackTrace();
        }
    }

    public synchronized boolean todaviaHaySoldados() {
        boolean termino = false;
        if (ejercito1.cantidadSoldados() <= 0 || ejercito2.cantidadSoldados() <= 0) {
            termino = true;
            notifyAll();
            String mensaje="Termino la batalla\nSoldados vivos:\n " +"Axis: "+ejercito1.cantidadSoldados()+" Urss: "+ejercito2.cantidadSoldados();
            setChanged();
            notifyObservers(mensaje);
            //Manda a la base de datos
            Dao acceso = new Dao();
            InformeBatalla i=new InformeBatalla();
            i.setResultadoFinal(mensaje);
            acceso.agregarBatalla(i);
        }
        return termino;
    }

    public Ejercito getEjercito2() {
        return ejercito2;
    }

    public void setEjercito2(Ejercito ejercito2) {
        this.ejercito2 = ejercito2;
    }

    public Ejercito getEjercito1() {
        return ejercito1;
    }

    public void setEjercito1(Ejercito ejercito1) {
        this.ejercito1 = ejercito1;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isGanador() {
        return ganador;
    }

    public void setGanador(boolean ganador) {
        this.ganador = ganador;
    }

    /**
     * Sets the delay between combat rounds (in milliseconds).
     * Used by web UI to allow time for animations.
     * Default is 0 (no delay) for console and JavaFX.
     * @param delayMs delay in milliseconds (0 = no delay)
     */
    public void setCombatDelayMs(long delayMs) {
        this.combatDelayMs = delayMs;
    }

    public long getCombatDelayMs() {
        return combatDelayMs;
    }

}
