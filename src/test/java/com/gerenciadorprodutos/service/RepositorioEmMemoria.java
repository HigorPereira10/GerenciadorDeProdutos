package com.gerenciadorprodutos.service;

import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dublê de teste (implementação falsa) do repositório - guarda tudo em uma
 * lista em memória, sem tocar em banco de dados nenhum. Usado para testar as
 * regras do ProdutoService de forma isolada e rápida, já que não depende de
 * SQLite nem de arquivos em disco.
 *
 * Isso só é possível porque o ProdutoService depende da interface
 * ProdutoRepository, e não diretamente da implementação SQLite.
 */
class RepositorioEmMemoria implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();
    // Simula o AUTOINCREMENT do banco de verdade: cada produto salvo recebe o próximo id disponível.
    private int proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        produto.setId(proximoId++);
        produtos.add(produto);
        return produto;
    }

    @Override
    public void atualizar(Produto produto) {
        // Encontra o produto existente pelo id e copia os novos valores para ele,
        // simulando um "UPDATE" de banco de dados.
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
        // Devolve uma cópia da lista, para que quem recebe não possa alterar
        // a lista interna deste repositório por acidente.
        return new ArrayList<>(produtos);
    }
}
