package Model;

import java.util.ArrayList;
import java.util.Date;

public class InformeBatalla {

    private int id;
    private String resultadoFinal;

    public InformeBatalla() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResultadoFinal() {
        return resultadoFinal;
    }

    public void setResultadoFinal(String resultadoFinal) {
        this.resultadoFinal = resultadoFinal;
    }

    @Override
    public String toString() {
        return "InformeBatalla{" +
                "id=" + id +
                ", resultadoFinal='" + resultadoFinal + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InformeBatalla that = (InformeBatalla) o;

        if (id != that.id) return false;
        return resultadoFinal != null ? resultadoFinal.equals(that.resultadoFinal) : that.resultadoFinal == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (resultadoFinal != null ? resultadoFinal.hashCode() : 0);
        return result;
    }
}
