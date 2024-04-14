package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyQueries2 {

  Connection con;
  JDBCUtilities settings;

  public MyQueries2(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

  public static void getMyData(Connection con) throws SQLException {
    Statement stmt = null;
    String query =
            "select c.nome_cliente, sum(d.saldo_deposito)\n" +
                    "from cliente c\n" +
                    "inner join deposito d on c.nome_cliente = d.nome_cliente\n" +
                    "group by c.nome_cliente";

    try {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
        System.out.println("Saldo total de cada cliente: ");
      while (rs.next()) {
        String cliente = rs.getString(1);
        String saldo = rs.getString(2);
        System.out.println("     " + cliente + "     " + saldo);
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

 	MyQueries2.getMyData(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
