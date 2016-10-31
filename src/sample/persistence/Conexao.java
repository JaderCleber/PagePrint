package sample.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by JaderCleber on 30/10/2016.
 */
public class Conexao {
    private Connection c = null;
    public Conexao(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:pageprint.db");
        } catch (Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void tabelaItem(){
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
//            c = DriverManager.getConnection("jdbc:sqlite:pageprint.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE ITEM " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " TIMAGEM         TEXT    NOT NULL, " +
                    " TCAMINHO        TEXT    NOT NULL, " +
                    " TDESCRICAO      TEXT    NOT NULL, " +
                    " IORDEM          INT, " +
                    " IPAGINA         INT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void tabelaCatalogo(){
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
//            c = DriverManager.getConnection("jdbc:sqlite:pageprint.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE PAGINA " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " TNOME         TEXT    NOT NULL, " +
                    " DCADASTRO     TEXT    NOT NULL, " +
                    " TDETALHES     TEXT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public boolean executar(String sql){
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    public ResultSet consultar(String sql){
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( sql );
            stmt.close();
            c.close();
            return rs;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            return null;
        }
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }
}
