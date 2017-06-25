package Model;

import Interfaz.*;

public class Soldado implements IAttack, IDefence {

    private long id;
    static long nextid = 0;
    private IAttack ataque;
    private IDefence defenza;

    public Soldado() {
        this.setId(nextid++);
        this.usarFusil();
        this.ponerseChaleco();
    }

    public void subirseAlAvion() {
        this.setAtaque(new AvionCombate());
    }

    public void ponerseChaleco() {
        this.setDefenza(new Chaleco());
    }

    public void usarFusil()
    {
        this.setAtaque(new Fusil());
    }

    public void escaparse()
    {
        this.setDefenza(new Correr());
    }

    public void subirseAlTanque() {
        this.setAtaque(new Tanque());
    }

    public void usarCañon() {
        this.setAtaque(new Cañon());
    }

    public void usarTrinchera() {
        this.setDefenza(new Trinchera());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public IAttack getAtaque() {
        return ataque;
    }

    public void setAtaque(IAttack ataque) {
        this.ataque = ataque;
    }

    public IDefence getDefenza() {
        return defenza;
    }

    public void setDefenza(IDefence defenza) {
        this.defenza = defenza;
    }

    @Override
    public int attack() {
        return this.getAtaque().attack();
    }

    @Override
    public int defence() {
        return this.getDefenza().defence();
    }

    @Override
    public String toString() {
        return "Soldado{" +
                "id=" + id +
                ", ataque=" + ataque +
                ", defenza=" + defenza +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Soldado soldado = (Soldado) o;

        if (id != soldado.id) return false;
        if (ataque != null ? !ataque.equals(soldado.ataque) : soldado.ataque != null) return false;
        return defenza != null ? defenza.equals(soldado.defenza) : soldado.defenza == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (ataque != null ? ataque.hashCode() : 0);
        result = 31 * result + (defenza != null ? defenza.hashCode() : 0);
        return result;
    }
}
