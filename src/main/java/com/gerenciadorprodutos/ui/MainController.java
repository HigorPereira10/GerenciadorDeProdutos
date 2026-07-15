package com.gerenciadorprodutos.ui;

import com.gerenciadorprodutos.exception.ValidacaoException;
import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.model.StatusEstoque;
import com.gerenciadorprodutos.repository.SqliteProdutoRepository;
import com.gerenciadorprodutos.service.ProdutoService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 * Controller da unica tela da aplicacao. Fala apenas com o ProdutoService -
 * nao sabe (nem precisa saber) que por baixo existe um banco SQLite.
 */
public class MainController implements Initializable {

    private final ProdutoService service = new ProdutoService(new SqliteProdutoRepository());
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    @FXML private BorderPane raiz;
    @FXML private Button btnTema;

    @FXML private Label lblTotalProdutos;
    @FXML private Label lblEstoqueBaixo;
    @FXML private Label lblValorTotal;
    @FXML private Label lblErro;
    @FXML private Label lblStatusBar;

    @FXML private TextField campoNome;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoPreco;
    @FXML private TextField campoBusca;

    @FXML private TableView<Produto> tabela;
    @FXML private TableColumn<Produto, Number> colunaId;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private TableColumn<Produto, Number> colunaQuantidade;
    @FXML private TableColumn<Produto, Number> colunaPreco;
    @FXML private TableColumn<Produto, Number> colunaValorTotal;
    @FXML private TableColumn<Produto, StatusEstoque> colunaStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunas();
        configurarBuscaEOrdenacao();
        configurarSelecaoDaTabela();
        carregarProdutos();
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(dados -> dados.getValue().idProperty());
        colunaNome.setCellValueFactory(dados -> dados.getValue().nomeProperty());
        colunaCategoria.setCellValueFactory(dados -> dados.getValue().categoriaProperty());
        colunaQuantidade.setCellValueFactory(dados -> dados.getValue().quantidadeProperty());
        colunaPreco.setCellValueFactory(dados -> dados.getValue().precoProperty());
        colunaValorTotal.setCellValueFactory(dados -> new ReadOnlyObjectWrapper<>(dados.getValue().getValorTotal()));
        colunaStatus.setCellValueFactory(dados -> new ReadOnlyObjectWrapper<>(dados.getValue().getStatus()));

        colunaPreco.setCellFactory(coluna -> celulaDeMoeda());
        colunaValorTotal.setCellFactory(coluna -> celulaDeMoeda());
        colunaStatus.setCellFactory(coluna -> celulaDeStatus());

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private TableCell<Produto, Number> celulaDeMoeda() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number valor, boolean vazio) {
                super.updateItem(valor, vazio);
                setText(vazio || valor == null ? null : moeda.format(valor));
            }
        };
    }

    private TableCell<Produto, StatusEstoque> celulaDeStatus() {
        return new TableCell<>() {
            @Override
            protected void updateItem(StatusEstoque status, boolean vazio) {
                super.updateItem(status, vazio);
                if (vazio || status == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label tag = new Label(status.getRotulo());
                tag.getStyleClass().addAll("tag-status", status.getEstiloCss());
                setGraphic(tag);
                setText(null);
            }
        };
    }

    /** Filtra a lista carregada em memoria a cada letra digitada - sem ida ao banco a cada tecla. */
    private void configurarBuscaEOrdenacao() {
        FilteredList<Produto> filtrados = new FilteredList<>(produtos, produto -> true);
        campoBusca.textProperty().addListener((obs, textoAntigo, texto) ->
                filtrados.setPredicate(produto -> texto == null || texto.isBlank()
                        || produto.getNome().toLowerCase().contains(texto.toLowerCase())));

        SortedList<Produto> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);
    }

    private void configurarSelecaoDaTabela() {
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });
    }

    @FXML
    private void adicionarProduto() {
        try {
            service.adicionar(campoNome.getText(), lerQuantidade(), lerPreco(), campoCategoria.getText());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto adicionado com sucesso.");
        } catch (ValidacaoException | NumberFormatException e) {
            mostrarErro(mensagemDeErro(e));
        }
    }

    @FXML
    private void atualizarProduto() {
        Produto selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um produto na tabela para atualizar.");
            return;
        }
        try {
            service.atualizar(selecionado.getId(), campoNome.getText(), lerQuantidade(), lerPreco(), campoCategoria.getText());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto atualizado com sucesso.");
        } catch (ValidacaoException | NumberFormatException e) {
            mostrarErro(mensagemDeErro(e));
        }
    }

    @FXML
    private void excluirProduto() {
        Produto selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um produto na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Excluir \"" + selecionado.getNome() + "\" do estoque? Essa acao nao pode ser desfeita.");
        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            service.excluir(selecionado.getId());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto excluido.");
        }
    }

    @FXML
    private void limparCampos() {
        campoNome.clear();
        campoCategoria.clear();
        campoQuantidade.clear();
        campoPreco.clear();
        tabela.getSelectionModel().clearSelection();
        esconderErro();
    }

    @FXML
    private void alternarTema() {
        boolean modoEscuroAtivo = raiz.getStyleClass().contains("modo-escuro");
        if (modoEscuroAtivo) {
            raiz.getStyleClass().remove("modo-escuro");
            btnTema.setText("Modo escuro");
        } else {
            raiz.getStyleClass().add("modo-escuro");
            btnTema.setText("Modo claro");
        }
    }

    private void carregarProdutos() {
        produtos.setAll(service.listarTodos());
        lblTotalProdutos.setText(String.valueOf(produtos.size()));
        lblEstoqueBaixo.setText(String.valueOf(service.contarEstoqueBaixo(produtos)));
        lblValorTotal.setText(moeda.format(service.calcularValorTotalEmEstoque(produtos)));
    }

    private void preencherFormulario(Produto produto) {
        campoNome.setText(produto.getNome());
        campoCategoria.setText(produto.getCategoria());
        campoQuantidade.setText(String.valueOf(produto.getQuantidade()));
        campoPreco.setText(String.valueOf(produto.getPreco()));
    }

    private int lerQuantidade() {
        String texto = campoQuantidade.getText();
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Informe a quantidade.");
        }
        return Integer.parseInt(texto.trim());
    }

    private double lerPreco() {
        String texto = campoPreco.getText();
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Informe o preco.");
        }
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    private String mensagemDeErro(Exception e) {
        return e instanceof NumberFormatException
                ? "Quantidade e preco devem ser numeros validos."
                : e.getMessage();
    }

    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void esconderErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    private void mostrarStatus(String mensagem) {
        esconderErro();
        lblStatusBar.setText(mensagem);
    }
}
