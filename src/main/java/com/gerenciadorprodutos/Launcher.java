package com.gerenciadorprodutos;

/**
 * Ponto de entrada real quando a aplicacao roda via classpath simples (por
 * exemplo o Run/Debug do VS Code). O JDK bloqueia a execucao direta de uma
 * classe que estende javafx.application.Application fora do module-path -
 * essa classe intermediaria (que nao estende Application) contorna essa
 * checagem e delega para o App de verdade.
 */
public class Launcher {

    public static void main(String[] args) {
        App.main(args);
    }
}
