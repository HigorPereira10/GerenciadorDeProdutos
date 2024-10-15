package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import database.ConexaoBD;

public class CriadorTabelas {

    public static void main(String[] args) {
        // Conectando ao banco de dados
        try (Connection conexao = ConexaoBD.conectar();
             Statement stmt = conexao.createStatement()) {

            // Comando SQL para criar a tabela "produtos"
            String comandoSQL = "CREATE TABLE IF NOT EXISTS produtos (" +
                    "id_produto INTEGER PRIMARY KEY AUTOINCREMENT," + // ID autoincrementável
                    "nome_produto TEXT NOT NULL," + // Nome do produto (não nulo)
                    "quantidade INTEGER," + // Quantidade de produtos
                    "preco REAL," + // Preço do produto
                    "status TEXT" + // Status do produto (ex: 'Estoque Normal', 'Estoque Baixo')
                    ");";

            // Executando o comando SQL
            stmt.execute(comandoSQL);
            System.out.println("Tabela 'produtos' criada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao criar a tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
