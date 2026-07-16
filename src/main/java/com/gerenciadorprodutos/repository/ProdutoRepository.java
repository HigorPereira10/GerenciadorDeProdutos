package com.gerenciadorprodutos.repository;

import com.gerenciadorprodutos.model.Produto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistência: define quais operações de banco de dados existem,
 * sem dizer como elas são feitas por dentro.
 *
 * A camada de serviço (ProdutoService) depende apenas desta interface, nunca da implementação SQLite diretamente.
 * Isso facilita trocar de banco de dados no futuro, ou criar uma implementação falsa (em memória) para os testes,
 * sem precisar de um banco de verdade - é exatamente o que a classe de teste RepositorioEmMemoria faz.
 */
public interface ProdutoRepository {

    /** Grava um produto novo no banco e devolve o mesmo produto já com o id gerado. */
    Produto salvar(Produto produto);

    /** Atualiza no banco os dados de um produto que já existe (identificado pelo id). */
    void atualizar(Produto produto);

    /** Remove do banco o produto com o id informado. */
    void excluir(int id);

    /** Busca um produto pelo id. Retorna Optional vazio se nenhum produto for encontrado. */
    Optional<Produto> buscarPorId(int id);

    /** Busca produtos cujo nome contenha o trecho informado. */
    List<Produto> buscarPorNome(String nome);

    /** Lista todos os produtos cadastrados. */
    List<Produto> listarTodos();
}
