package AccesoDatos;

import Model.InformeBatalla;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class connectDatabase {

    private Connection connection;
    private Statement sentencia;
    private ResultSet resultado;
    static connectDatabase instance;


    public static connectDatabase getInstance()
    {
        if(instance==null)
        {
            instance=new connectDatabase();
        }
        return  instance;
    }

    public void openConnection()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            Logger.getLogger(connectDatabase.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/batallas", "root", "123456");
        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }

    public void closeConnection()
    {
        try{
            connection.close();
        }
        catch (SQLException e)
        {
            Logger.getLogger(connectDatabase.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarInforme(InformeBatalla i)
    {
        try {
            this.openConnection();
            String query = "INSERT INTO INFORMES(resultado) VALUES(?)";
            PreparedStatement st=connection.prepareStatement(query);
            st.setString(1,i.getResultadoFinal());
            st.execute();
            System.out.println("Guardado en la base de datos\n");
            this.closeConnection();
        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
    }

    public ArrayList<InformeBatalla> traerLista()
    {
        ArrayList<InformeBatalla> lista=null;

        try {
            this.openConnection();
            String query = "Select * from informes";
            sentencia = connection.createStatement();
            resultado=sentencia.executeQuery(query);
            lista=new ArrayList<InformeBatalla>();

            while (resultado.next())
            {
                InformeBatalla i=new InformeBatalla();
                i.setId(resultado.getInt("id"));
                i.setResultadoFinal(resultado.getNString("resultado"));
                lista.add(i);
            }

            this.closeConnection();
        }
        catch (SQLException e)
        {
            e.getStackTrace();
        }
        return lista;
    }
}
