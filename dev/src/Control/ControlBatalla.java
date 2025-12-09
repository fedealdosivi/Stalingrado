package Control;

import AccesoDatos.Dao;
import Model.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ControlBatalla implements Observer {

    Ejercito axis = new Ejercito("AXIS");
    Ejercito urss = new Ejercito("URSS");
    CampoBatalla Stalingrado = new CampoBatalla();
    Dao acceso = new Dao();
    Scanner teclado = new Scanner(System.in);

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Nueva Actualizacion: " + o + " -> " + arg);
    }

    public void printearLista() {
        ArrayList<InformeBatalla> listaInformes = acceso.traerLista();
        for (InformeBatalla i : listaInformes) {
            System.out.println(i.toString());
        }
    }

    public void asignarCampo() {
        this.Stalingrado.agregarObservador(this);
    }

    public void combatir() {
        axis.start();
        urss.start();
    }

    public void prepararEjercitos() {
        axis.setCampo(Stalingrado);
        urss.setCampo(Stalingrado);
        Stalingrado.setEjercito1(axis);
        Stalingrado.setEjercito2(urss);
    }

    public int preguntarCantidad() {
        System.out.println("Ingrese la cantidad");
        int c = teclado.nextInt();
        return c;
    }

    public String preguntarBando() {
        System.out.println("Ingrese el bando URSS o AXIS");
        String b = teclado.next();
        return b.toUpperCase();
    }

    public double preguntarBonif() {
        System.out.println("Ingrese la bonificacion");
        double c = teclado.nextDouble();
        return c;
    }

    public void cambiarBonificacionDefensa(String bando, double bonif) {
        if (axis.getBando().equals(bando)) {
            axis.setBonifdefensa(bonif);
        } else if (urss.getBando().equals(bando)){
            urss.setBonifdefensa(bonif);
        }
    }

    public void cambiarBonificacionAtaque(String bando, double bonif) {
        if (axis.getBando().equals(bando)) {
            axis.setBonifataque(bonif);
        } else if (urss.getBando().equals(bando)) {
            {
                urss.setBonifataque(bonif);
            }
        }
    }

    public void agregarTanques(int cant, String bando) {

        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarTanque();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarTanque();
                cant--;
            }
        }
    }

    public void agregarAviones(int cant, String bando) {

        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarAvion();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarAvion();
                cant--;
            }
        }

    }

    public void agregarFusileros(int cant, String bando) {

        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarFusilero();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarFusilero();
                cant--;
            }
        }
    }

    public void agregarCañoneros(int cant, String bando) {

        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarCañonero();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarCañonero();
                cant--;
            }
        }
    }

    public void agregarTrincheros(int cant, String bando) {
        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarTrinchero();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarTrinchero();
                cant--;
            }
        }

    }

    public void agregarCobardes(int cant, String bando) {
        if (axis.getBando().equals(bando)) {
            while (cant != 0) {
                axis.agregarCobarde();
                cant--;
            }
        } else if (urss.getBando().equals(bando)) {
            while (cant != 0) {
                urss.agregarCobarde();
                cant--;
            }
        }
    }

    public void printearSoldados(String bando) {
        if (axis.getBando().equals(bando)) {
            axis.printSoldados();
        } else if (urss.getBando().equals(bando)) {
            urss.printSoldados();
        }
    }

}
