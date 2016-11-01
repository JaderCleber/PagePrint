/**
 * Copyright 2016 Jader Cleber
 *
 * This file is part of PagePrint.
 *
 * PagePrint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PagePrint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package sample.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by JaderCleber on 30/10/2016.
 */
public class Conexao {
    private Connection con = null;

    public Conexao() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:pageprint.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

//    public void tabelaItem(){
//        Statement stmt = null;
//        try {
//            Class.forName("org.sqlite.JDBC");
////            con = DriverManager.getConnection("jdbc:sqlite:pageprint.db");
//
//            stmt = con.createStatement();
//            String sql = "CREATE TABLE ITEM " +
//                    "(ID INT PRIMARY KEY     NOT NULL," +
//                    " TIMAGEM         TEXT    NOT NULL, " +
//                    " TCAMINHO        TEXT    NOT NULL, " +
//                    " TDESCRICAO      TEXT    NOT NULL, " +
//                    " IORDEM          INT, " +
//                    " IPAGINA         INT)";
//            stmt.executeUpdate(sql);
//            stmt.close();
//            con.close();
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//            System.exit(0);
//        }
//    }
//
//    public void tabelaCatalogo(){
//        Statement stmt = null;
//        try {
//            Class.forName("org.sqlite.JDBC");
////            con = DriverManager.getConnection("jdbc:sqlite:pageprint.db");
//
//            stmt = con.createStatement();
//            String sql = "CREATE TABLE PAGINA " +
//                    "(ID INT PRIMARY KEY     NOT NULL," +
//                    " TNOME         TEXT    NOT NULL, " +
//                    " DCADASTRO     TEXT    NOT NULL, " +
//                    " TDETALHES     TEXT)";
//            stmt.executeUpdate(sql);
//            stmt.close();
//            con.close();
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//            System.exit(0);
//        }
//    }

    public boolean executar(String sql) {
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con.setAutoCommit(false);

            stmt = con.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
            con.commit();
            con.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ResultSet consultar(String sql, Statement stmt) {
        try {
            Class.forName("org.sqlite.JDBC");
            con.setAutoCommit(false);

            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            stmt.close();
            con.close();
            return rs;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public int novoCatalogo(String sql, String dataCadastro) {
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con.setAutoCommit(false);
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            con.commit();

            ResultSet idGerado = stmt.executeQuery(" SELECT ID FROM CATALOGO WHERE TCADASTRO = '" +dataCadastro + "'");
//            idGerado.first();
            int resultado = 0;
            while(idGerado.next()){
                resultado = idGerado.getInt("ID");
            }

            stmt.close();
            con.close();
            return resultado;
        } catch (Exception e) {
            return 0;
        }
    }
}
