package com.oracle.tutorial.jdbc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MyQueries {
  
  Connection con;
  JDBCUtilities settings;  
  
  public MyQueries(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

  public static void getMyData(Connection con) throws SQLException {
    Statement stmt = null;
    String query =
            "SELECT SUPPLIERS.SUP_NAME, Count(COFFEES.COF_NAME) FROM COFFEES, SUPPLIERS " +
                    "WHERE SUPPLIERS.SUP_ID = COFFEES.SUP_ID " +
                    "GROUP BY SUPPLIERS.SUP_NAME";

    try {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
        System.out.println("Types of coffee from each supplier: ");
      while (rs.next()) {
        String supplierName = rs.getString(1);
        String count = rs.getString(2);
        System.out.println("     " + supplierName + "     " + count);
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      if (stmt != null) { stmt.close(); }
    }
  }

  public static void populateTable(Connection con) throws SQLException, IOException {
    BufferedReader inputStream = null;
    Scanner scanned_line = null;
    String line;
    String[] value;
    value = new String[7];
    Statement stmt = null;
    int countv;
    StringBuilder create = new StringBuilder();

    try {
      stmt = con.createStatement();
      System.out.printf("Executando DDL/DML:");

      try {
        inputStream = new BufferedReader(new FileReader("/tmp/debito-populate-table.txt"));
        while ((line = inputStream.readLine()) != null) {
          countv = 0;
          scanned_line = new Scanner(line);
          scanned_line.useDelimiter("\t");
          while (scanned_line.hasNext()) {
            value[countv++] = scanned_line.next();
          }
          if (scanned_line != null) {
            scanned_line.close();
          }
          create.append("insert into debito" + " (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente)" + " values (")
                  .append(value[0]).append(", ").append(value[1]).append(", '").append(value[2]).append("', '").append(value[3])
                  .append("', ").append(value[4]).append(", '").append(value[5]).append("', '").append(value[6]).append("');");
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }

      stmt.executeUpdate("truncate table debito");
      stmt.executeUpdate(create.toString());
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      if (stmt != null) { stmt.close(); }
    }
  }

  public static void main(String[] args) {
    JDBCUtilities myJDBCUtilities;
    Connection myConnection = null;
    if (args[0] == null) {
      System.err.println("Properties file not specified at command line");
      return;
    } else {
      try {
        myJDBCUtilities = new JDBCUtilities(args[0]);
      } catch (Exception e) {
        System.err.println("Problem reading properties file " + args[0]);
        e.printStackTrace();
        return;
      }
    }

    try {
      myConnection = myJDBCUtilities.getConnection();

     MyQueries.populateTable(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
