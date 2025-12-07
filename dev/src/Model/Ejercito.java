package Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercito extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Ejercito.class);
    private static final AtomicInteger nextId = new AtomicInteger(0);

    private final List<Soldado> listaSoldados = Collections.synchronizedList(new ArrayList<>());
    private final String bando;
    private final int idejercito;
    private volatile double bonifataque;
    private volatile double bonifdefensa;
    private volatile CampoBatalla campo;

    public Ejercito(String bando) {
        this.bando = bando;
        this.idejercito = nextId.getAndIncrement();
        if (getBando().equals("USSR")) {
            this.setBonifataque(0.95);
            this.setBonifdefensa(1.95);
        } else {
            this.setBonifdefensa(1);
            this.setBonifataque(2.95);
        }
    }

    public String enfrentarse(Ejercito enemigo) {
        String mensaje = "";
        if (haySoldados() && enemigo.haySoldados()) {

            Soldado s1 = this.randomSoldado();
            Soldado s2 = enemigo.randomSoldado();

            int atacante = this.calcularAtaque(s1);
            int defensor = enemigo.calcularDefensa(s2);

            if (atacante > defensor) {
                enemigo.eliminarSoldado(s2);
                mensaje = mensaje + "El soldado " + s2.getId() + " del bando " + enemigo.getBando() + " fue asesinado con " + s1.getAtaque().toString();
            }
            if (atacante < defensor) {
                this.eliminarSoldado(s1);
                mensaje = mensaje + "El soldado " + s1.getId() + " del bando " + this.getBando() + " fue asesinado con " + s2.getAtaque().toString();
            }
            if (atacante == defensor) {
                mensaje = mensaje + "No funciono el ataque";
            }
        }
        return mensaje;
    }

    public int calcularAtaque(Soldado s) {
        double result = s.attack() * this.getBonifataque();
        int ataque = (int) result;
        return ataque;
    }

    public int calcularDefensa(Soldado s) {
        double result = s.defence() * this.getBonifdefensa();
        int defensa = (int) result;
        return defensa;
    }

    public Soldado randomSoldado() {
        synchronized (listaSoldados) {
            if (listaSoldados.isEmpty()) {
                return null;
            }
            int randomIndex = ThreadLocalRandom.current().nextInt(listaSoldados.size());
            return listaSoldados.get(randomIndex);
        }
    }

    @Override
    public void run() {
        logger.info("Army {} starting battle with {} soldiers", bando, cantidadSoldados());
        try {
            while (!campo.isGanador() && !Thread.currentThread().isInterrupted()) {
                campo.combate(this.getBando());
                Thread.sleep(1000);
            }
            logger.info("Army {} finished battle with {} soldiers remaining", bando, cantidadSoldados());
        } catch (InterruptedException e) {
            logger.warn("Army {} battle interrupted", bando);
            Thread.currentThread().interrupt();
        }
    }

    public String getBando() {
        return bando;
    }

    public List<Soldado> getListaSoldados() {
        return new ArrayList<>(listaSoldados);
    }

    public void setListaSoldados(List<Soldado> soldados) {
        synchronized (listaSoldados) {
            this.listaSoldados.clear();
            this.listaSoldados.addAll(soldados);
        }
    }

    public double getBonifataque() {
        return bonifataque;
    }

    public void setBonifataque(double bonifataque) {
        this.bonifataque = bonifataque;
    }

    public double getBonifdefensa() {
        return bonifdefensa;
    }

    public void setBonifdefensa(double bonifdefensa) {
        this.bonifdefensa = bonifdefensa;
    }

    public CampoBatalla getCampo() {
        return campo;
    }

    public void setCampo(CampoBatalla campo) {
        this.campo = campo;
    }

    public void agregarSoldado(Soldado objSoldado) {
        listaSoldados.add(objSoldado);
    }

    public void eliminarSoldado(Soldado soldado) {
        listaSoldados.remove(soldado);
    }

    public void printSoldados() {
        for (Soldado s : listaSoldados) {
            System.out.println(s.toString());
        }
    }

    public int cantidadSoldados() {
        int cant = 0;
        if (this.getListaSoldados() != null) {
            for (Soldado s : listaSoldados
                    ) {
                cant++;
            }
        }
        return cant;
    }

    public Soldado getSoldadoByID(long id) {
        Soldado s = null;
        for (int x = 0; x < listaSoldados.size(); x++) {
            if (listaSoldados.get(x).getId() == id) {
                s = listaSoldados.get(x);
            }
        }
        return s;
    }

    public Soldado getSoldadoByPos(int pos) {
        return listaSoldados.get(pos);
    }

    public boolean haySoldados() {
        boolean rta = false;
        if (this.getListaSoldados() != null) {
            if (this.cantidadSoldados() >= 1) {
                rta = true;
            }
        }
        return rta;
    }

    public int getIdejercito() {
        return idejercito;
    }

    public void agregarTanque() {
        Soldado objS = new Soldado();
        objS.subirseAlTanque();
        this.agregarSoldado(objS);
    }

    public void agregarAvion() {
        Soldado objS = new Soldado();
        objS.subirseAlAvion();
        this.agregarSoldado(objS);
    }

    public void agregarFusilero() {
        Soldado objS = new Soldado();
        objS.usarFusil();
        this.agregarSoldado(objS);
    }

    public void agregarCañonero() {
        Soldado objS = new Soldado();
        objS.usarCañon();
        this.agregarSoldado(objS);
    }

    public void agregarTrinchero() {
        Soldado objS = new Soldado();
        objS.usarTrinchera();
        this.agregarSoldado(objS);
    }

    public void agregarCobarde() {
        Soldado objS = new Soldado();
        objS.escaparse();
        this.agregarSoldado(objS);
    }

    @Override
    public String toString() {
        return "Ejercito{" +
                "listaSoldados=" + listaSoldados +
                ", bando='" + bando + '\'' +
                ", idejercito=" + idejercito +
                ", bonifataque=" + bonifataque +
                ", bonifdefensa=" + bonifdefensa +
                ", campo=" + campo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ejercito ejercito = (Ejercito) o;

        if (idejercito != ejercito.idejercito) return false;
        if (Double.compare(ejercito.bonifataque, bonifataque) != 0) return false;
        if (Double.compare(ejercito.bonifdefensa, bonifdefensa) != 0) return false;
        if (listaSoldados != null ? !listaSoldados.equals(ejercito.listaSoldados) : ejercito.listaSoldados != null)
            return false;
        if (bando != null ? !bando.equals(ejercito.bando) : ejercito.bando != null) return false;
        return campo != null ? campo.equals(ejercito.campo) : ejercito.campo == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = listaSoldados != null ? listaSoldados.hashCode() : 0;
        result = 31 * result + (bando != null ? bando.hashCode() : 0);
        result = 31 * result + idejercito;
        temp = Double.doubleToLongBits(bonifataque);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bonifdefensa);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (campo != null ? campo.hashCode() : 0);
        return result;
    }
}
