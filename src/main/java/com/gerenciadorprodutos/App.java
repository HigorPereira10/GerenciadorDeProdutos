package com.gerenciadorprodutos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada da aplicação JavaFX. Só monta a janela principal -
 * toda a logica de tela fica no MainController (que, ao ser criado pelo
 * FXMLLoader, ja cuida de preparar o banco de dados).
 */
public class App extends Application {

    @Override
    public void start(Stage palco) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gerenciadorprodutos/ui/main-view.fxml"));
        Parent raiz = loader.load();

        Scene cena = new Scene(raiz, 1120, 700);
        cena.getStylesheets().add(getClass().getResource("/com/gerenciadorprodutos/ui/styles.css").toExternalForm());

        palco.setTitle("Gerenciador de Produtos");
        palco.setScene(cena);
        palco.setMinWidth(960);
        palco.setMinHeight(620);
        palco.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
