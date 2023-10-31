package db;

import java.sql.*;

public class AuthService {


    private Connection connection = null;

    private Statement statement = null;
    private PreparedStatement preparedStatement = null;


    public AuthService() {


        String url = "jdbc:mysql://" + Database.host + ":" + Database.port + "/" + Database.dbName + "?useUnicode=true&characterEncoding=utf8" ;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        }

        try {
            connection = DriverManager.getConnection(url , Database.username , Database.password);
            System.out.println("Successfully Connected.");

        } catch (SQLException e) {
            System.out.println("Error while connecting to database");
        }

    }


    public int login(String username , String password) {

        String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";

        try {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1 , username);
            preparedStatement.setString(2 , password);

            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;

    }

    public void register(String username , String password) {

        String query = "INSERT INTO accounts (username , password , balance) VALUE(? , ? , ?)";

        try {
            preparedStatement = connection.prepareStatement(query);

            double balance = 0;

            preparedStatement.setString(1 , username);
            preparedStatement.setString(2 , password);
            preparedStatement.setDouble(3 , balance);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }




}
