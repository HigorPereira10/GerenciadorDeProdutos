package com.gerenciadorprodutos.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(int id) {
        super("Nenhum produto encontrado com o id " + id);
    }
}
