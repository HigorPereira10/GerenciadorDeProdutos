package main;

import javax.swing.SwingUtilities;

import view.ProdutoView;

public class AppMain {
    public static void main(String[] args) {
        // Chama a interface do usuário
        SwingUtilities.invokeLater(() -> {
            ProdutoView view = new ProdutoView();
            view.setVisible(true);
        });
    }
}
