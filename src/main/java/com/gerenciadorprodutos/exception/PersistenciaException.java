package com.gerenciadorprodutos.exception;

/**
 * Exceção lançada quando alguma operação de banco de dados falha 
 * (por exemplo, ao salvar, buscar ou excluir um produto no SQLite).
 *
 * Ela encapsula qualquer SQLException que suba da camada de repositório. 
 * A ideia é que o restante da aplicação (serviço e tela) não precise saber que existe um banco SQL por trás, 
 * assim, se um dia trocarmos de banco de dados, só essa camada de repositório precisa mudar.
 *
 * É uma RuntimeException (exceção "não verificada"), então quem chama não é
 * obrigado a declarar "throws" nem a usar try/catch, a menos que queira
 * tratar o erro de forma especial.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
