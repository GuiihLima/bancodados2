package com.oracle.tutorial.jdbc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
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
            if (stmt != null) {
                stmt.close();
            }
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
            con.setAutoCommit(false);
            stmt = con.createStatement();
            stmt.executeUpdate("truncate table debito");
            System.out.printf("Executando DDL/DML:");

            inputStream = new BufferedReader(new FileReader("/home/guiih/workspace/tmp/debito-populate-table.txt"));
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
            stmt.executeUpdate(create.toString());
            con.setAutoCommit(true);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (stmt != null) {
                stmt.close();

            }
        }
    }

    public static void getMyData3(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "select c.nome_cliente, \n" +
                "\tcoalesce( sum( d.saldo_deposito ), 0 ) as deposito,\t\n" +
                "\tcoalesce( sum( e.valor_emprestimo ), 0 ) as emprestimo\n" +
                "from conta c\n" +
                "left outer join deposito d on c.numero_conta = d.numero_conta \n" +
                "left outer join emprestimo e on c.numero_conta = e.numero_conta \n" +
                "group by c.nome_cliente, c.nome_agencia, c.numero_conta \n" +
                "order by c.nome_cliente";

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Saldo de deposito e emprestimo por cliente: ");
            while (rs.next()) {
                String nome_cliente = rs.getString("nome_cliente");
                Double deposito = rs.getDouble("deposito");
                Double emprestimo = rs.getDouble(3);
                System.out.println("     " + nome_cliente + "     " + deposito + "     " + emprestimo);
            }
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void cursorHoldabilitySupport(Connection con) throws SQLException {
        DatabaseMetaData dbMetaData = con.getMetaData();

        System.out.printf("Supports ResultSetConcurrency TYPE_FORWARD_ONLY and CONCUR_READ_ONLY: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        System.out.printf("Supports ResultSetConcurrency TYPE_SCROLL_INSENSITIVE and CONCUR_READ_ONLY: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
        System.out.printf("Supports ResultSetConcurrency TYPE_SCROLL_SENSITIVE and CONCUR_READ_ONLY: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));
        System.out.printf("Supports ResultSetConcurrency TYPE_FORWARD_ONLY and CONCUR_UPDATABLE: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        System.out.printf("Supports ResultSetConcurrency TYPE_SCROLL_INSENSITIVE and CONCUR_UPDATABLE: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
        System.out.printf("Supports ResultSetConcurrency TYPE_SCROLL_SENSITIVE and CONCUR_UPDATABLE: %b%n",
                dbMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
    }

    public static void modifyPrices(Connection con) throws SQLException {
        Statement stmt = null;
        System.out.println("Digite o multiplicador como um numero real: (Ex.: 5% = 1.05):");
        Scanner in = new Scanner(System.in);
        double percentage = in.nextDouble();

        try {
            stmt = con.createStatement();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = stmt.executeQuery("SELECT * FROM COFFEES");
            System.out.println("Preço dos cafés: ");

            while (uprs.next()) {
                System.out.println(uprs.getString("COF_NAME") + " " + uprs.getFloat("PRICE"));
                float f = uprs.getFloat("PRICE");
                uprs.updateFloat("PRICE", f * (float) percentage);
                uprs.updateRow();
                System.out.printf("Preço do cafe apos aumento de %.2f%%: %.2f%n", ((percentage - 1) * 100), uprs.getFloat("PRICE"));
            }
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void insertRow(Connection con, int numero_debito, float valor_debito,
                                 int motivo_debito, String data_debito, int numero_conta,
                                 String nome_agencia, String nome_cliente) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = stmt.executeQuery("SELECT * FROM DEBITO");
            uprs.moveToInsertRow();
            uprs.updateInt("numero_debito", numero_debito);
            uprs.updateFloat("valor_debito", valor_debito);
            uprs.updateInt("motivo_debito", motivo_debito);
            uprs.updateDate("data_debito", Date.valueOf(data_debito));
            uprs.updateInt("numero_conta", numero_conta);
            uprs.updateString("nome_agencia", nome_agencia);
            uprs.updateString("nome_cliente", nome_cliente);
            uprs.insertRow();
            uprs.beforeFirst();
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void insertMyData1(Connection con) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        String query = null;
        query = "insert into debito (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " +
                "values (3000, 3000, 5, '2014-02-06', 36593, 'UFU', 'Pedro Alvares Sousa')";
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            if (stmt != null) {
                stmt.close();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Debitos da Instituicao Bancaria atualizados.");
            System.out.println("Um debito em IB2 inserido em " + (endTime - startTime) + " milisegundos");

        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        }
    }

    public static void insertMyData2(Connection con) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        String query = null;
        query = "insert into debito (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try {
            stmt = con.prepareStatement(query);
            stmt.setInt(1, 3001);
            stmt.setDouble(2, 3001);
            stmt.setInt(3, 4);
            stmt.setDate(4, Date.valueOf("2014-02-06"));
            stmt.setInt(5, 36593);
            stmt.setString(6, "UFU");
            stmt.setString(7, "Pedro Alvares Sousa");
            stmt.executeUpdate();
            if (stmt != null) {
                stmt.close();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Debitos da Instituicao Bancaria atualizados usando PreparedStatement.");
            System.out.println("Um debito em IB2 inserido em " + (endTime - startTime) + " milisegundos");
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        }
    }

    public static void insertMyData1000(Connection con) throws SQLException {
        con.setAutoCommit(false);
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        String query = null;
        try {
            for (int num = 3002; num < 4002; num++) {
                query = "insert into debito (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " +
                        "values (" + num + ", " + num + ", 5, '2014-02-06', 36593, 'UFU', 'Pedro Alvares Sousa')";
                stmt = con.createStatement();
                stmt.executeUpdate(query);
                if (stmt != null) {
                    stmt.close();
                }
                if ((num % 50) == 0) {
                    long endTime = System.currentTimeMillis();
                    System.out.println(num - 3000 + "\t" + (endTime - startTime));
                }
            }
            con.commit();
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        }
    }

    public static void insertMyData2000(Connection con) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        String query = null;
        query = "insert into debito (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        for (int num = 5002; num < 6002; num++) {
            try {
                stmt = con.prepareStatement(query);
                stmt.setInt(1, num);
                stmt.setDouble(2, num);
                stmt.setInt(3, 5);
                stmt.setDate(4, Date.valueOf("2014-02-06"));
                stmt.setInt(5, 36593);
                stmt.setString(6, "UFU");
                stmt.setString(7, "Pedro Alvares Sousa");
                stmt.executeUpdate();
                if (stmt != null) {
                    stmt.close();
                }
                if ((num % 50) == 0) {
                    long endTime = System.currentTimeMillis();
                    System.out.println(num - 5000 + "\t" + (endTime - startTime));
                }
            } catch (SQLException e) {
                JDBCUtilities.printSQLException(e);
            }
        }
    }

    public static void insertMyData3000(Connection con) throws SQLException {
        long startTime = System.currentTimeMillis();
        PreparedStatement stmt = null;
        String query = null;
        query = "insert into debito (numero_debito, valor_debito, motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try {
            con.setAutoCommit(false);
            stmt = con.prepareStatement(query);
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        }

        for (int num = 5002; num < 6002; num++) {
            stmt.setInt(1, num);
            stmt.setDouble(2, num);
            stmt.setInt(3, 5);
            stmt.setDate(4, Date.valueOf("2014-02-06"));
            stmt.setInt(5, 36593);
            stmt.setString(6, "UFU");
            stmt.setString(7, "Pedro Alvares Sousa");
            stmt.executeUpdate();
            if ((num % 50) == 0) {
                long endTime = System.currentTimeMillis();
                System.out.println(num - 5000 + "\t" + (endTime - startTime));
            }
        }

        try {
            con.commit();
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
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

//            MyQueries.cursorHoldabilitySupport(myConnection);
            MyQueries.populateTable(myConnection);
//            MyQueries.getMyData3(myConnection);
//            MyQueries.modifyPrices(myConnection);
//            MyQueries.insertRow(myConnection, 2000, 150, 1, "2014-01-23", 46248, "UFU", "Carla Soares Sousa");
//            MyQueries.insertRow(myConnection, 2001, 200, 2, "2014-01-23", 26892, "Glória", "Carolina Soares Souza");
//            MyQueries.insertRow(myConnection, 2002, 500, 3, "2014-01-23", 70044, "Cidade Jardim", "Eurides Alves da Silva");
//            MyQueries.insertMyData1(myConnection);
//            MyQueries.insertMyData2(myConnection);
//            MyQueries.insertMyData1000(myConnection);
//            MyQueries.insertMyData2000(myConnection);
//            MyQueries.insertMyData3000(myConnection);

        } catch (SQLException e) {
            JDBCUtilities.printSQLException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtilities.closeConnection(myConnection);
        }

    }
}
