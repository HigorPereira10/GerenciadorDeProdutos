package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.ConexaoBD;

public class ProdutoDAO {

    // Método para gerar o próximo ID disponível
    private int gerarProximoIdDisponivel() {
        String sql = "SELECT id_produto FROM produtos ORDER BY id_produto ASC";
        try (Connection conexao = ConexaoBD.conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int id = 1; // Iniciar o ID com 1
            while (rs.next()) {
                // Se o ID atual no ResultSet não corresponder ao esperado, significa que esse ID está disponível
                if (rs.getInt("id_produto") != id) {
                    break;
                }
                id++; // Incrementa o ID se estiver em ordem
            }
            return id; // Retorna o primeiro ID disponível

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Retorna 1 como fallback se houver erro
    }

    // Método para adicionar um produto ao banco de dados
    public void adicionarProduto(Produto produto) {
        String sql = "INSERT INTO produtos (id_produto, nome_produto, quantidade, preco, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            // Gerar ID antes de adicionar o produto
            int proximoId = gerarProximoIdDisponivel();
            produto.setId(proximoId);

            pstmt.setInt(1, produto.getId());
            pstmt.setString(2, produto.getNome());
            pstmt.setInt(3, produto.getQuantidade());
            pstmt.setDouble(4, produto.getPreco());
            pstmt.setString(5, produto.getStatus());

            pstmt.executeUpdate();
            System.out.println("Produto adicionado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir um produto do banco de dados
    public void excluirProduto(int id) {
        String sql = "DELETE FROM produtos WHERE id_produto = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Produto excluído com sucesso!");
            } else {
                System.out.println("Nenhum produto encontrado com o ID fornecido.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar um produto no banco de dados
    public void atualizarProduto(Produto produto) {
        String sql = "UPDATE produtos SET nome_produto = ?, quantidade = ?, preco = ?, status = ? WHERE id_produto = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setInt(2, produto.getQuantidade());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setString(4, produto.getStatus());
            pstmt.setInt(5, produto.getId());

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Produto atualizado com sucesso!");
            } else {
                System.out.println("Nenhum produto encontrado com o ID fornecido.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar um produto pelo ID
    public Produto buscarProdutoPorId(int id) {
        String sql = "SELECT * FROM produtos WHERE id_produto = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setStatus(rs.getString("status"));

                return produto; // Retorna o produto encontrado
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retorna null se não encontrar o produto
    }

    // Método para buscar produtos pelo nome
    public List<Produto> buscarProdutosPorNome(String nome) {
        String sql = "SELECT * FROM produtos WHERE nome_produto LIKE ?";
        List<Produto> listaDeProdutos = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nome + "%"); // Usar LIKE para busca parcial
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setStatus(rs.getString("status"));

                listaDeProdutos.add(produto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaDeProdutos; // Retorna a lista de produtos encontrados
    }

    // Método para buscar todos os produtos
    public List<Produto> listarTodosProdutos() {
        String sql = "SELECT * FROM produtos";
        List<Produto> listaDeProdutos = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setStatus(rs.getString("status"));

                listaDeProdutos.add(produto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaDeProdutos; // Retorna a lista de produtos
    }
}