package com.gerenciadorprodutos.repository;

import com.gerenciadorprodutos.model.Produto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia. A camada de servico depende apenas desta
 * interface, nao da implementacao SQLite - facilita trocar de banco
 * ou criar uma implementacao falsa para testes, se um dia for preciso.
 */
public interface ProdutoRepository {

    Produto salvar(Produto produto);

    void atualizar(Produto produto);

    void excluir(int id);

    Optional<Produto> buscarPorId(int id);

    List<Produto> buscarPorNome(String nome);

    List<Produto> listarTodos();
}
