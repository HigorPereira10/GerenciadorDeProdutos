package com.gerenciadorprodutos.service;

import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dublê de teste do repositório - guarda tudo em uma lista, sem tocar em banco
 * nenhum. Usado para testar as regras do ProdutoService isoladamente e rápido.
 */
class RepositorioEmMemoria implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        produto.setId(proximoId++);
        produtos.add(produto);
        return produto;
    }

    @Override
    public void atualizar(Produto produto) {
        buscarPorId(produto.getId()).ifPresent(existente -> {
            existente.setNome(produto.getNome());
            existente.setQuantidade(produto.getQuantidade());
            existente.setPreco(produto.getPreco());
            existente.setCategoria(produto.getCategoria());
        });
    }

    @Override
    public void excluir(int id) {
        produtos.removeIf(produto -> produto.getId() == id);
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        return produtos.stream().filter(produto -> produto.getId() == id).findFirst();
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        return produtos.stream()
                .filter(produto -> produto.getNome().toLowerCase().contains(nome.toLowerCase()))
                .toList();
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }
}
