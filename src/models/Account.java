package models;

import java.util.ArrayList;


public class Account {


    private double balance;
    private ArrayList<Object[]> transactionHistory = new ArrayList<>(10);

    public Account(double balance, ArrayList<Object[]> transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addTransactionToHistory(Object[] operation) {
        transactionHistory.add(operation);
    }

    public ArrayList<Object[]> getTransactionHistory() {
        return transactionHistory;
    }
}
