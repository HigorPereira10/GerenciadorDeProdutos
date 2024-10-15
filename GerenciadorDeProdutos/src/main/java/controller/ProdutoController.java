package controller;

import model.Produto;
import model.ProdutoDAO;

import java.util.List;

public class ProdutoController {
    private ProdutoDAO produtoDAO;

    public ProdutoController() {
        this.produtoDAO = new ProdutoDAO();
    }

    // Método para adicionar um produto
    public void adicionarProduto(String nome, int quantidade, double preco, String status) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setQuantidade(quantidade);
        produto.setPreco(preco);
        produto.setStatus(status);
        produtoDAO.adicionarProduto(produto);
    }

    // Método para excluir um produto
    public void excluirProduto(int id) {
        produtoDAO.excluirProduto(id);
    }

    // Método para atualizar um produto
    public void atualizarProduto(int id, String nome, int quantidade, double preco, String status) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setQuantidade(quantidade);
        produto.setPreco(preco);
        produto.setStatus(status);
        produtoDAO.atualizarProduto(produto);
    }

    // Método para buscar produto por ID
    public Produto buscarProdutoPorId(int id) {
        return produtoDAO.buscarProdutoPorId(id);
    }

    // Método para buscar produtos pelo nome
    public List<Produto> buscarProdutosPorNome(String nome) {
        return produtoDAO.buscarProdutosPorNome(nome);
    }

    // Método para listar todos os produtos
    public List<Produto> listarTodosProdutos() {
        return produtoDAO.listarTodosProdutos();
    }
}
