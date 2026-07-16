package com.gerenciadorprodutos.exception;

/**
 * Exceção lançada quando os dados de um produto não passam nas regras de negócio 
 * (nome vazio, quantidade negativa, preço negativo etc).
 *
 * É uma RuntimeException de propósito: quem chama a camada de serviço não é
 * obrigado a lidar com "checked exceptions" (aquelas que exigem try/catch ou throws obrigatório) 
 * só para poder mostrar uma mensagem amigável na tela.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
