package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author ING JARC
 */
public class ConexionBD {

public  String puerto="3306";
//public  String nomservidor="localhost";
//public  String nomservidor="sql5.freemysqlhosting.net";
public  String nomservidor="db4free.net";
//public  String db="servicioing";
//public  String db="sql5108911";
//public  String db="ssing";
public String db="abarrotera";
//public  String user="alexander92";
//public  String user="sql5108911";
//public  String user="ssing";
public String user="oswaldo";
//public  String pass="shark123";
//public  String pass="Rd3DwnStke";
//public  String pass="shark123";
public String pass="oswaldohuerta";
//Connection conn=null;

private Connection conn;

    public Connection getConexion() {
        return conn;
    }

    public void setConexion(Connection conexion) {
        this.conn = conexion;
    }

public Connection conectar(){
    try{
    //String ruta="jdbc:mysql://";
    //String servidor=nomservidor+":"+puerto+"/";
//    String BaseDeDatos = "jdbc:mysql://23.229.190.232/ServicioIng?connectTimeout=3000";
    

    //String BaseDeDatos = "jdbc:mysql://23.229.190.232/ServicioIng";
    //String BaseDeDatos = "jdbc:mysql://localhost/servicioing?user=root&password=";
    //String BaseDeDatos = "jdbc:mysql://sql5.freemysqlhosting.net/sql5108911?user=sql5108911&password=Rd3DwnStke";
    //+"?autoReconnect=true&useSSL=false "
    String BaseDeDatos = "jdbc:mysql://"+nomservidor+"/"+db+"?user="+user+"&password="+pass;
    Class.forName("com.mysql.jdbc.Driver");
    conn = DriverManager.getConnection(BaseDeDatos);
//    setConexion(DriverManager.getConnection(BaseDeDatos));

    if (conn!=null){
    System.out.println("Conexión correcta a la base de datos.");
    }
    else if (conn==null)
    {
    throw new SQLException();
    }
    }catch(SQLException e){
        JOptionPane.showMessageDialog(null, e.getMessage());
//        JOptionPane.showMessageDialog(null, "Se ha agotado el tiempo de espera para conectarse a la base de datos.",
//            "Advertencia", JOptionPane.WARNING_MESSAGE);
    } catch (ClassNotFoundException e) {
        JOptionPane.showMessageDialog(null, "Se produjo el siguiente error: "+e.getMessage());
    }catch (NullPointerException e){
        JOptionPane.showMessageDialog(null, "Se produjo el siguiente error: "+e.getMessage());
    }finally{
    return conn;
    }
}
public void desconectar(){
    conn = null;
    System.out.println("Desconexion correcta de la base de datos.");
}
}
