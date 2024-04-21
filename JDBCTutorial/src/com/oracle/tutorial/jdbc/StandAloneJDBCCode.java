package com.oracle.tutorial.jdbc;

import java.sql.*;
import java.util.Properties;

public class StandAloneJDBCCode {
    public static Connection getConnection() throws SQLException {
        Connection con = null;
        String currentUrlString = null;
        Properties connectionProps = new Properties();

        connectionProps.put("user", "postgres");
        connectionProps.put("password", "postgres");
        currentUrlString = "jdbc:postgresql://localhost:5432/IB2";
        System.out.println();

        try {
            con = DriverManager.getConnection(currentUrlString, connectionProps);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
            System.out.println("Released all database resources.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void myQuery(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "SELECT * FROM debito; ";

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Table list: ");
            while (rs.next()) {
                int numeroDebito = rs.getInt(1);
                int valorDebito = rs.getInt(2);
                int motivoDebito = rs.getInt(3);
                Date dataDebito = rs.getDate(4);
                int numeroConta = rs.getInt(5);
                String nomeAgencia = rs.getString(6);
                String nomeCliente = rs.getString(7);
                System.out.println(numeroDebito + ", " + valorDebito + ", " + motivoDebito + ", " + dataDebito
                        + ", " + numeroConta + ", " + nomeAgencia + ", " + nomeCliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("No arguments.");
        }

        Connection myConnection = null;
        try {
            myConnection = StandAloneJDBCCode.getConnection();
            StandAloneJDBCCode.myQuery(myConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            StandAloneJDBCCode.closeConnection(myConnection);
        }
    }
}
