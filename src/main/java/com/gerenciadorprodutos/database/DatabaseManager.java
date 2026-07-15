package com.gerenciadorprodutos.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Cuida da conexao com o SQLite e da criacao da tabela. Recebe a URL de
 * conexao de fora (em vez de ter um caminho fixo "gravado na pedra") para
 * que os testes possam apontar para um banco temporario, sem tocar no
 * estoque.db real usado pela aplicacao.
 */
public final class DatabaseManager {

    /** Arquivo usado pela aplicacao em producao, criado ao lado do jar. */
    public static final String URL_PADRAO = "jdbc:sqlite:estoque.db";

    private DatabaseManager() {
    }

    /** Cria a tabela de produtos caso ainda nao exista nessa base. */
    public static void criarTabelas(String urlConexao) {
        try (Connection conexao = obterConexao(urlConexao);
             Statement comando = conexao.createStatement()) {
            comando.execute(lerScriptDeCriacao());
        } catch (SQLException e) {
            throw new IllegalStateException("Nao foi possivel preparar o banco de dados", e);
        }
    }

    public static Connection obterConexao(String urlConexao) throws SQLException {
        return DriverManager.getConnection(urlConexao);
    }

    private static String lerScriptDeCriacao() {
        try (InputStream entrada = DatabaseManager.class.getResourceAsStream("/schema.sql")) {
            if (entrada == null) {
                throw new IllegalStateException("Arquivo schema.sql nao encontrado no classpath");
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler schema.sql", e);
        }
    }
}
