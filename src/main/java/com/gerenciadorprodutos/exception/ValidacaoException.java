package com.gerenciadorprodutos.exception;

/**
 * Lançada quando os dados de um produto não passam nas regras de negócio
 * (nome vazio, quantidade negativa, etc). É RuntimeException de propósito:
 * quem chama a camada de serviço não é obrigado a lidar com "checked
 * exceptions" só para poder mostrar uma mensagem amigável na tela.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
