package com.gerenciadorprodutos;

/**
 * Ponto de entrada real quando a aplicação roda via classpath simples (por exemplo o Run/Debug do VS Code).
 * O JDK bloqueia a execução direta de uma classe que estende javafx.application.Application fora do module-path -
 * essa classe intermediária (que não estende Application) contorna essa checagem e delega para o App de verdade.
 *
 * Ou seja: para rodar a aplicação pelo VS Code, o "Run" deve apontar para esta classe (Launcher), e não diretamente para a App.
 */
public class Launcher {

    public static void main(String[] args) {
        App.main(args);
    }
}
