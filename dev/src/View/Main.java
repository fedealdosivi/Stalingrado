package View;

import Model.*;
import Control.*;
import AccesoDatos.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ControlBatalla controladora = null;
        int rta = 0;
        Scanner teclado = new Scanner(System.in);

        while (rta != -1) {

            System.out.println("Menu:");
            System.out.println("1-Inicializar Ejercitos");
            System.out.println("2-Modificar bonificacion de Ataque");
            System.out.println("3-Modificar bonificacion de Defensa");
            System.out.println("4-Mostrar soldados de los Ejercitos");
            System.out.println("5-Cargar Aviones");
            System.out.println("6-Cargar Tanques");
            System.out.println("7-Cargar Cañones");
            System.out.println("8-Cargar Fusileros");
            System.out.println("9-Cargar Trincheros");
            System.out.println("10-Cargar Cobardes");
            System.out.println("11-Combatir");
            System.out.println("12-Ver todas las batallas");
            System.out.println("13-Salir");

            rta = teclado.nextInt();

            switch (rta) {
                case 1:
                    controladora = new ControlBatalla();
                    controladora.prepararEjercitos();
                    controladora.asignarCampo();
                    break;

                case 2:
                    controladora.cambiarBonificacionAtaque(controladora.preguntarBando(), controladora.preguntarBonif());
                    break;

                case 3:
                    controladora.cambiarBonificacionDefensa(controladora.preguntarBando(), controladora.preguntarBonif());
                    break;

                case 4:
                    controladora.printearSoldados(controladora.preguntarBando());
                    break;
                case 5:
                    controladora.agregarAviones(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 6:
                    controladora.agregarTanques(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 7:
                    controladora.agregarCañoneros(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 8:
                    controladora.agregarFusileros(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 9:
                    controladora.agregarTrincheros(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 10:
                    controladora.agregarCobardes(controladora.preguntarCantidad(), controladora.preguntarBando());
                    break;

                case 11:
                    controladora.combatir();
                    break;

                case 12:
                    controladora.printearLista();
                    break;

                case 13:
                    rta = -1;
                    System.exit(1);
                    break;
            }

        }


    }
}
