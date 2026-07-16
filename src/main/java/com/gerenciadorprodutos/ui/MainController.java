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
 * Controller da única tela da aplicação (ligado ao arquivo main-view.fxml).
 * Responsável por dar vida ao layout visual: reagir a cliques de botão, preencher a tabela, mostrar mensagens de erro etc.
 *
 * Esta classe fala apenas com o ProdutoService. Cada campo marcado com @FXML é injetado automaticamente pelo JavaFX 
 * a partir de um componente com o mesmo "fx:id" no arquivo main-view.fxml.
 */
public class MainController implements Initializable {

    // Instancia direto o repositório SQLite e o serviço.
    private final ProdutoService service = new ProdutoService(new SqliteProdutoRepository());
    // Lista "observável": qualquer componente do JavaFX (como a tabela) pode "escutar" mudanças nela e se atualizar sozinho quando ela muda.
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();
    // Formata valores em Real (R$).
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    // Componentes do cabeçalho da tela (raiz do layout e botão de trocar o tema).
    @FXML private BorderPane raiz;
    @FXML private Button btnTema;

    // Rótulos (labels) do dashboard e das mensagens de status/erro.
    @FXML private Label lblTotalProdutos;
    @FXML private Label lblEstoqueBaixo;
    @FXML private Label lblValorTotal;
    @FXML private Label lblErro;
    @FXML private Label lblStatusBar;

    // Campos de texto do formulário de cadastro/edição e do campo de busca.
    @FXML private TextField campoNome;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoQuantidade;
    @FXML private TextField campoPreco;
    @FXML private TextField campoBusca;

    // Tabela de produtos e suas colunas. 
    // O tipo genérico <Produto, X> indica que cada coluna mostra, para cada Produto da lista, um valor do tipo X.
    @FXML private TableView<Produto> tabela;
    @FXML private TableColumn<Produto, Number> colunaId;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private TableColumn<Produto, Number> colunaQuantidade;
    @FXML private TableColumn<Produto, Number> colunaPreco;
    @FXML private TableColumn<Produto, Number> colunaValorTotal;
    @FXML private TableColumn<Produto, StatusEstoque> colunaStatus;

    /**
     * Chamado automaticamente pelo JavaFX assim que o FXML termina de ser carregado e todos os campos @FXML já foram preenchidos. 
     * É o lugar certo para qualquer configuração inicial da tela.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunas();
        configurarBuscaEOrdenacao();
        configurarSelecaoDaTabela();
        carregarProdutos();
    }

    /**
     * Liga cada coluna da tabela à propriedade correspondente do Produto (cellValueFactory)
     * e define como o valor deve ser exibido visualmente (cellFactory), quando o padrão não é suficiente (moeda, tag de status).
     */
    private void configurarColunas() {
        colunaId.setCellValueFactory(dados -> dados.getValue().idProperty());
        colunaNome.setCellValueFactory(dados -> dados.getValue().nomeProperty());
        colunaCategoria.setCellValueFactory(dados -> dados.getValue().categoriaProperty());
        colunaQuantidade.setCellValueFactory(dados -> dados.getValue().quantidadeProperty());
        colunaPreco.setCellValueFactory(dados -> dados.getValue().precoProperty());
        // Preço x quantidade e status não são propriedades do Produto (são calculados),
        // por isso usam um ReadOnlyObjectWrapper: um "envelope" só de leitura para o valor.
        colunaValorTotal.setCellValueFactory(dados -> new ReadOnlyObjectWrapper<>(dados.getValue().getValorTotal()));
        colunaStatus.setCellValueFactory(dados -> new ReadOnlyObjectWrapper<>(dados.getValue().getStatus()));

        colunaPreco.setCellFactory(coluna -> celulaDeMoeda());
        colunaValorTotal.setCellFactory(coluna -> celulaDeMoeda());
        colunaStatus.setCellFactory(coluna -> celulaDeStatus());

        // Faz as colunas dividirem o espaço disponível da tabela, "esticando" a última coluna para preencher o que sobrar.
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    /** Cria uma célula de tabela que formata números como moeda (R$ 0,00) em vez de texto puro. */
    private TableCell<Produto, Number> celulaDeMoeda() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number valor, boolean vazio) {
                super.updateItem(valor, vazio);
                // "vazio" é true em linhas sem dados (ex.: linhas em branco no fim da tabela).
                setText(vazio || valor == null ? null : moeda.format(valor));
            }
        };
    }

    /** Cria uma célula de tabela que mostra o status como uma "tag" colorida, em vez de texto puro. */
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
                // Em vez de texto simples, a célula exibe um Label estilizado via CSS
                // (a classe "tag-esgotado"/"tag-baixo"/"tag-normal" define a cor de fundo).
                Label tag = new Label(status.getRotulo());
                tag.getStyleClass().addAll("tag-status", status.getEstiloCss());
                setGraphic(tag);
                setText(null);
            }
        };
    }

    /** Filtra a lista carregada em memória a cada letra digitada - sem ida ao banco a cada tecla. */
    private void configurarBuscaEOrdenacao() {
        // FilteredList começa mostrando todo mundo (predicado "sempre verdadeiro").
        FilteredList<Produto> filtrados = new FilteredList<>(produtos, produto -> true);
        // Sempre que o texto do campo de busca mudar, troca o filtro aplicado à lista.
        campoBusca.textProperty().addListener((obs, textoAntigo, texto) ->
                filtrados.setPredicate(produto -> texto == null || texto.isBlank()
                        || produto.getNome().toLowerCase().contains(texto.toLowerCase())));

        // SortedList reordena a lista já filtrada, seguindo o clique do usuário no cabeçalho da tabela
        // (comparatorProperty é o critério de ordenação atual).
        SortedList<Produto> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenados);
    }

    /** Quando o usuário clica em uma linha da tabela, preenche o formulário com os dados dela. */
    private void configurarSelecaoDaTabela() {
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });
    }

    /**
     * Ação do botão "Adicionar". Lê os campos do formulário, tenta cadastrar
     * o produto e trata qualquer erro de validação/número inválido mostrando
     * uma mensagem amigável, em vez de deixar a exceção "estourar".
     */
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

    /** Ação do botão "Atualizar". Exige que um produto esteja selecionado na tabela. */
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

    /**
     * Ação do botão "Excluir". Exige um produto selecionado e pede confirmação antes de excluir de fato,
     * já que é uma ação que não pode ser desfeita.
     */
    @FXML
    private void excluirProduto() {
        Produto selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um produto na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Excluir \"" + selecionado.getNome() + "\" do estoque? Essa acao nao pode ser desfeita.");
        // showAndWait() pausa a execução até o usuário clicar em algum botão do alerta.
        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            service.excluir(selecionado.getId());
            carregarProdutos();
            limparCampos();
            mostrarStatus("Produto excluido.");
        }
    }

    /** Ação do botão "Limpar". Esvazia o formulário e desmarca qualquer seleção na tabela. */
    @FXML
    private void limparCampos() {
        campoNome.clear();
        campoCategoria.clear();
        campoQuantidade.clear();
        campoPreco.clear();
        tabela.getSelectionModel().clearSelection();
        esconderErro();
    }

    /** Ação do botão de tema. Alterna a classe CSS "modo-escuro" na raiz da tela. */
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

    /**
     * Recarrega a lista de produtos a partir do serviço e atualiza os indicadores do dashboard 
     * (total de produtos, estoque baixo, valor total). Chamado sempre que os dados mudam (adicionar/atualizar/excluir). 
     */
    private void carregarProdutos() {
        produtos.setAll(service.listarTodos());
        lblTotalProdutos.setText(String.valueOf(produtos.size()));
        lblEstoqueBaixo.setText(String.valueOf(service.contarEstoqueBaixo(produtos)));
        lblValorTotal.setText(moeda.format(service.calcularValorTotalEmEstoque(produtos)));
    }

    /** Copia os dados de um produto selecionado para os campos do formulário, para edição. */
    private void preencherFormulario(Produto produto) {
        campoNome.setText(produto.getNome());
        campoCategoria.setText(produto.getCategoria());
        campoQuantidade.setText(String.valueOf(produto.getQuantidade()));
        campoPreco.setText(String.valueOf(produto.getPreco()));
    }

    /** Lê e converte o campo de quantidade para número inteiro, validando que não está vazio. */
    private int lerQuantidade() {
        String texto = campoQuantidade.getText();
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Informe a quantidade.");
        }
        return Integer.parseInt(texto.trim());
    }

    /**
     * Lê e converte o campo de preço para número decimal, validando que não está vazio.
     * Troca vírgula por ponto antes de converter, para aceitar o formato brasileiro de números decimais (ex.: "5,90").
     */
    private double lerPreco() {
        String texto = campoPreco.getText();
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException("Informe o preco.");
        }
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    /** Traduz uma exceção capturada em uma mensagem amigável para exibir na tela. */
    private String mensagemDeErro(Exception e) {
        return e instanceof NumberFormatException
                ? "Quantidade e preco devem ser numeros validos."
                : e.getMessage();
    }

    /** Exibe uma mensagem de erro na tela, tornando o rótulo de erro visível. */
    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    /** Esconde a mensagem de erro (tanto visualmente quanto no espaço que ela ocupa no layout). */
    private void esconderErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    /** Exibe uma mensagem de sucesso/status na barra inferior, escondendo qualquer erro anterior. */
    private void mostrarStatus(String mensagem) {
        esconderErro();
        lblStatusBar.setText(mensagem);
    }
}
