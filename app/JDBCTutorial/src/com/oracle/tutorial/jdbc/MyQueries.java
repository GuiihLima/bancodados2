package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyQueries {
  
  Connection con;
  JDBCUtilities settings;  
  
  public MyQueries(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

//  public static void getMyData(String supplierName, Connection con) throws SQLException {
//    Statement stmt = null;
//    String query =
//            "SELECT COFFEES.COF_NAME FROM COFFEES, SUPPLIERS WHERE SUPPLIERS.SUP_NAME LIKE '" +
//                    supplierName + "' and SUPPLIERS.SUP_ID = COFFEES.SUP_ID";
//
//    try {
//      stmt = con.createStatement();
//      ResultSet rs = stmt.executeQuery(query);
//      System.out.println("Coffees bought from " + supplierName + ": ");
//      while (rs.next()) {
//        String coffeeName = rs.getString(1);
//        System.out.println("     " + coffeeName);
//      }
//    } catch (SQLException e) {
//      JDBCUtilities.printSQLException(e);
//    } finally {
//      if (stmt != null) { stmt.close(); }
//    }
//  }

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

 	MyQueries.getMyData(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
