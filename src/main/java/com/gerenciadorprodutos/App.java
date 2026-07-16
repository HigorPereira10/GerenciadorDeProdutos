package com.gerenciadorprodutos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada da aplicação JavaFX. 
 *
 * Essa classe só monta a janela principal (carrega o FXML e o CSS);
 * toda a lógica de tela de fato fica no MainController, que é criado automaticamente pelo FXMLLoader e,
 * ao ser criado, já cuida de preparar o banco de dados.
 */
public class App extends Application {

    /**
     * Chamado automaticamente pelo JavaFX assim que a aplicação inicia.
     * "palco" (Stage) é a janela do sistema operacional em si; "cena" (Scene) é o conteúdo que fica dentro dela.
     */
    @Override
    public void start(Stage palco) throws Exception {
        // Carrega o layout da tela a partir do arquivo FXML (definido visualmente, como um "HTML" da interface)
        // e o transforma numa árvore de componentes JavaFX.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gerenciadorprodutos/ui/main-view.fxml"));
        Parent raiz = loader.load();

        // Cria a cena com o tamanho inicial da janela (largura x altura em pixels)
        // e aplica a folha de estilos (CSS) que define cores, fontes etc.
        Scene cena = new Scene(raiz, 1120, 700);
        cena.getStylesheets().add(getClass().getResource("/com/gerenciadorprodutos/ui/styles.css").toExternalForm());

        // Configura o título da janela, o conteúdo (cena) e o tamanho mínimo permitido, e por fim exibe a janela na tela.
        palco.setTitle("Gerenciador de Produtos");
        palco.setScene(cena);
        palco.setMinWidth(960);
        palco.setMinHeight(620);
        palco.show();
    }

    /**
     * Método main "de verdade" da aplicação. launch(args) é um método herdado de Application que prepara todo o ambiente do JavaFX e,
     *  em seguida, chama o método start() acima.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
