package DBConnectivity;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public Connection connection;
    public Connection getConnection(){
        String db = "projectw1.team69";
        String username = "root";
        String password = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/"+db,username,password);
        }catch(Exception e){
            e.printStackTrace();
        }
        return connection;
    }

}
