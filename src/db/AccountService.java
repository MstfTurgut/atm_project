package db;

import models.Account;

import java.sql.*;
import java.util.ArrayList;

public class AccountService {

    public Account account;
    private final int id;
    private Connection connection = null;

    private Statement statement = null;
    private PreparedStatement preparedStatement = null;

    public AccountService(int id) {
        this.id = id;

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

        // declare account object from database

        account = getAccount();

    }

    public Account getAccount() {

        String query1 = "SELECT balance FROM accounts WHERE id = ?";
        String query2 = "SELECT type,amount FROM transactionhistories WHERE account_id = ?";

        try {
            double balance = 0;

            preparedStatement = connection.prepareStatement(query1);

            preparedStatement.setInt(1  , id);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                balance = rs.getDouble("balance");
            }

            preparedStatement = connection.prepareStatement(query2);

            preparedStatement.setInt(1 , id);

            ResultSet rs2 = preparedStatement.executeQuery();

            ArrayList<Object[]> historyArray = new ArrayList<>();

            while (rs2.next()) {
                Object[] transaction = new Object[2];

                transaction[0] = rs2.getString("type");
                transaction[1] = rs2.getDouble("amount");

                historyArray.add(transaction);
            }

            return new Account(balance , historyArray);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }

    public void depositMoney(double amount) {

        // database balance change

        double newBalance = account.getBalance() + amount;

        String query = "UPDATE accounts SET balance = ? WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setDouble(1 , newBalance);
            preparedStatement.setInt(2 , id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // account balance change

        account.setBalance(newBalance);

        // database history table change

        String query2 = "INSERT INTO transactionhistories (account_id ,type , amount) VALUE(?,?,?)";

        String type = "Deposit";

        try {
            preparedStatement = connection.prepareStatement(query2);

            preparedStatement.setInt(1,id);
            preparedStatement.setString(2 , type);
            preparedStatement.setDouble(3 , amount);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // database history table change

        Object[] newTransaction = {type , amount};

        account.addTransactionToHistory(newTransaction);

    }

    public void withdrawMoney(double amount) {

        // database balance change

        double newBalance = account.getBalance() - amount;

        String query = "UPDATE accounts SET balance = ? WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setDouble(1 , newBalance);
            preparedStatement.setInt(2 , id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // account balance change

        account.setBalance(newBalance);

        // database history table change

        String type = "Withdraw";

        String query2 = "INSERT INTO transactionhistories (account_id ,type , amount) VALUE(?,?,?)";

        try {
            preparedStatement = connection.prepareStatement(query2);

            preparedStatement.setInt(1 , id);
            preparedStatement.setString(2 , type);
            preparedStatement.setDouble(3  , amount);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // account history table change

        Object[] transaction = {type , amount};
        account.addTransactionToHistory(transaction);

    }

    public void transferMoney(int targetId , double amount) {

        // database balance change

        double account1NewBalance = account.getBalance() - amount;

        String query = "UPDATE accounts SET balance = ? WHERE id = ?";

        try {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setDouble(1 , account1NewBalance);
            preparedStatement.setInt(2 , id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // account balance change

        account.setBalance(account1NewBalance);

        // database th change

        String type = "Transferred to ID : " + targetId;

        String query2 = "INSERT INTO transactionhistories (account_id ,type , amount) VALUE(?,?,?)";

        try {
            preparedStatement = connection.prepareStatement(query2);

            preparedStatement.setInt(1,id);
            preparedStatement.setString(2 , type);
            preparedStatement.setDouble(3 , amount);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // account th change

        Object[] transaction = {type , amount};
        account.addTransactionToHistory(transaction);

        // database2 balance change

        String query3 = "SELECT balance FROM accounts WHERE id = " + targetId;

        try {
            statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(query3);

            double targetBalance = 0;

            while (rs.next()) {
                targetBalance = rs.getDouble("balance");
            }

            double targetBalanceAfter = targetBalance + amount;

            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setDouble(1 , targetBalanceAfter);
            preparedStatement.setInt(2 , targetId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // database2 th change

        String type2 = "Transferred from ID : " + id;

        try {
            preparedStatement = connection.prepareStatement(query2);

            preparedStatement.setInt(1 , targetId);
            preparedStatement.setString(2 , type2);
            preparedStatement.setDouble(3 , amount);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
