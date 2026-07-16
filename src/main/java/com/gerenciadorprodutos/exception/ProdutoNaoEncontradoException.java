package com.gerenciadorprodutos.exception;


// Exceção lançada quando se tenta buscar, atualizar ou excluir um produto usando um id que não existe no banco de dados.

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(int id) {
        // A mensagem já vem pronta com o id informado, para facilitar a identificação do problema em uma tela de erro ou em um log.
        super("Nenhum produto encontrado com o id " + id);
    }
}
