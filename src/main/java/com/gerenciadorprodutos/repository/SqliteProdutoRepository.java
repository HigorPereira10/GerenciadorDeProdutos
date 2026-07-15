package com.gerenciadorprodutos.repository;

import com.gerenciadorprodutos.database.DatabaseManager;
import com.gerenciadorprodutos.exception.PersistenciaException;
import com.gerenciadorprodutos.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteProdutoRepository implements ProdutoRepository {

    private final String urlConexao;

    /** Usada pela aplicacao: grava no arquivo estoque.db padrao. */
    public SqliteProdutoRepository() {
        this(DatabaseManager.URL_PADRAO);
    }

    /** Usada pelos testes: aponta para um banco temporario, isolado do estoque.db real. */
    public SqliteProdutoRepository(String urlConexao) {
        this.urlConexao = urlConexao;
        DatabaseManager.criarTabelas(urlConexao);
    }

    private Connection conectar() throws SQLException {
        return DatabaseManager.obterConexao(urlConexao);
    }

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, quantidade, preco, categoria) VALUES (?, ?, ?, ?)";

        try (Connection conexao = conectar();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherParametros(comando, produto);
            comando.executeUpdate();

            // Deixa o SQLite (AUTOINCREMENT) gerar o id, em vez de calcular manualmente
            // o "proximo id livre" como na versao antiga - mais simples e sem condicoes de corrida.
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
            return produto;

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao salvar o produto '" + produto.getNome() + "'", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, quantidade = ?, preco = ?, categoria = ? WHERE id = ?";

        try (Connection conexao = conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preencherParametros(comando, produto);
            comando.setInt(5, produto.getId());
            comando.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao atualizar o produto de id " + produto.getId(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conexao = conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            comando.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao excluir o produto de id " + id, e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id = ?";

        try (Connection conexao = conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar o produto de id " + id, e);
        }
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        String sql = "SELECT * FROM produtos WHERE nome LIKE ? ORDER BY nome";

        try (Connection conexao = conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, "%" + nome + "%");
            try (ResultSet resultado = comando.executeQuery()) {
                return mapearTodos(resultado);
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao buscar produtos pelo nome '" + nome + "'", e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT * FROM produtos ORDER BY nome";

        try (Connection conexao = conectar();
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {

            return mapearTodos(resultado);

        } catch (SQLException e) {
            throw new PersistenciaException("Falha ao listar os produtos", e);
        }
    }

    private void preencherParametros(PreparedStatement comando, Produto produto) throws SQLException {
        comando.setString(1, produto.getNome());
        comando.setInt(2, produto.getQuantidade());
        comando.setDouble(3, produto.getPreco());
        comando.setString(4, produto.getCategoria());
    }

    private List<Produto> mapearTodos(ResultSet resultado) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        while (resultado.next()) {
            produtos.add(mapear(resultado));
        }
        return produtos;
    }

    private Produto mapear(ResultSet resultado) throws SQLException {
        Produto produto = new Produto();
        produto.setId(resultado.getInt("id"));
        produto.setNome(resultado.getString("nome"));
        produto.setQuantidade(resultado.getInt("quantidade"));
        produto.setPreco(resultado.getDouble("preco"));
        produto.setCategoria(resultado.getString("categoria"));
        return produto;
    }
}
