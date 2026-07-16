package com.gerenciadorprodutos.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária responsável por tudo que envolve a conexão com o SQLite:
 * abrir conexões e garantir que a tabela de produtos exista.
 *
 * A URL de conexão é recebida de fora (por parâmetro), em vez de ter um caminho fixo dentro da classe. 
 * Isso permite que os testes automatizados apontem para um banco de dados temporário, sem nunca tocar no arquivo estoque.db real.
 */
public final class DatabaseManager {

    /** Caminho do arquivo de banco usado pela aplicação em produção, criado ao lado do jar. */
    public static final String URL_PADRAO = "jdbc:sqlite:estoque.db";

    // Construtor privado: como todos os métodos são estáticos, essa classe nunca precisa ser instanciada.
    private DatabaseManager() {
    }

    /**
     * Garante que a tabela de produtos exista no banco indicado por urlConexao.
     * Se a tabela já existir, o comando SQL (que usa "IF NOT EXISTS") não faz nada, 
     * por isso é seguro chamar este método toda vez que a aplicação inicia.
     */
    public static void criarTabelas(String urlConexao) {
        // O "try-with-resources" abaixo garante que a conexão e o Statement sejam
        // fechados automaticamente ao final do bloco, mesmo se ocorrer um erro.
        try (Connection conexao = obterConexao(urlConexao);
             Statement comando = conexao.createStatement()) {
            comando.execute(lerScriptDeCriacao());
        } catch (SQLException e) {
            // Qualquer falha aqui impede a aplicação de funcionar, então é lançada
            // como uma exceção não verificada (RuntimeException) para interromper a inicialização.
            throw new IllegalStateException("Nao foi possivel preparar o banco de dados", e);
        }
    }

    /** Abre uma nova conexão JDBC com o banco SQLite indicado pela URL. */
    public static Connection obterConexao(String urlConexao) throws SQLException {
        return DriverManager.getConnection(urlConexao);
    }

    /**
     * Lê o conteúdo do arquivo schema.sql (que fica dentro dos recursos do projeto,
     * em src/main/resources) e devolve como texto, para ser executado como comando SQL.
     */
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
