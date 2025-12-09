package Control;

import AccesoDatos.Dao;
import Model.*;

import java.util.ArrayList;

public class ControlBatallaUI {

    private Ejercito axis;
    private Ejercito urss;
    private CampoBatalla Stalingrado;
    private Dao acceso;

    public ControlBatallaUI() {
        this.acceso = new Dao();
    }

    public void inicializarEjercitos() {
        this.axis = new Ejercito("AXIS");
        this.urss = new Ejercito("URSS");
        this.Stalingrado = new CampoBatalla();
        prepararEjercitos();
    }

    private void prepararEjercitos() {
        axis.setCampo(Stalingrado);
        urss.setCampo(Stalingrado);
        Stalingrado.setEjercito1(axis);
        Stalingrado.setEjercito2(urss);
    }

    public void cambiarBonificacionDefensa(String bando, double bonif) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            ejercito.setBonifdefensa(bonif);
        }
    }

    public void cambiarBonificacionAtaque(String bando, double bonif) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            ejercito.setBonifataque(bonif);
        }
    }

    public void agregarTanques(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarTanque();
            }
        }
    }

    public void agregarAviones(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarAvion();
            }
        }
    }

    public void agregarFusileros(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarFusilero();
            }
        }
    }

    public void agregarCañoneros(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarCañonero();
            }
        }
    }

    public void agregarTrincheros(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarTrinchero();
            }
        }
    }

    public void agregarCobardes(int cant, String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            for (int i = 0; i < cant; i++) {
                ejercito.agregarCobarde();
            }
        }
    }

    public String obtenerSoldados(String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        if (ejercito != null) {
            StringBuilder info = new StringBuilder();
            info.append("Ejercito: ").append(ejercito.getBando()).append("\n");
            info.append("Total soldados: ").append(ejercito.cantidadSoldados()).append("\n\n");
            for (int i = 0; i < ejercito.getListaSoldados().size(); i++) {
                info.append(ejercito.getSoldadoByPos(i).toString()).append("\n");
            }
            return info.toString();
        }
        return "Ejercito no encontrado";
    }

    public void combatir() {
        if (axis != null && urss != null) {
            axis.start();
            urss.start();
        }
    }

    public ArrayList<InformeBatalla> obtenerListaBatallas() {
        return acceso.traerLista();
    }

    public boolean isInicializado() {
        return axis != null && urss != null && Stalingrado != null;
    }

    public int getCantidadSoldados(String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        return ejercito != null ? ejercito.cantidadSoldados() : 0;
    }

    public double getBonifAtaque(String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        return ejercito != null ? ejercito.getBonifataque() : 0.0;
    }

    public double getBonifDefensa(String bando) {
        Ejercito ejercito = obtenerEjercito(bando);
        return ejercito != null ? ejercito.getBonifdefensa() : 0.0;
    }

    private Ejercito obtenerEjercito(String bando) {
        String bandoUpper = bando.toUpperCase();
        if ("AXIS".equals(bandoUpper)) {
            return axis;
        } else if ("URSS".equals(bandoUpper)) {
            return urss;
        }
        return null;
    }
}