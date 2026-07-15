package com.gerenciadorprodutos.exception;

/**
 * Envolve qualquer SQLException que suba da camada de repositório.
 * A ideia é o restante da aplicação não precisar saber que existe um
 * banco SQL por trás - se um dia trocarmos de banco, só essa camada muda.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
