package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL_JDBC_PADRAO = "jdbc:sqlite:meu_banco_de_dados.db";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL_JDBC_PADRAO);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }
}
