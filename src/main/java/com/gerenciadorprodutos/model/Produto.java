package com.gerenciadorprodutos.model;

import javafx.beans.property.*;

/**
 * Entidade de domínio: representa um produto do estoque.
 *
 * Em vez de usar campos simples (int, String, double), esta classe usa
 * "propriedades" do JavaFX (IntegerProperty, StringProperty etc). 
 * Porque a TableView da interface observa essas propriedades diretamente: quando um valor muda 
 * (por exemplo, a quantidade de um produto), 
 * a célula da tabela se atualiza sozinha na tela, sem precisar recarregar a lista inteira manualmente.
 *
 * Cada campo segue sempre o mesmo padrão de 3 membros:
 *  - um getX(), que devolve o valor "puro" (int, String, double...);
 *  - um setX(valor), que altera o valor;
 *  - um xProperty(), que devolve a propriedade JavaFX em si, usada pela tela
 *    para "escutar" mudanças.
 */
public class Produto {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final IntegerProperty quantidade = new SimpleIntegerProperty();
    private final DoubleProperty preco = new SimpleDoubleProperty();
    private final StringProperty categoria = new SimpleStringProperty();

    /** Construtor vazio, usado quando os dados serão preenchidos depois. */
    public Produto() {
    }

    /** Construtor de conveniência, usado ao criar um produto novo já com todos os dados em mãos. */
    public Produto(String nome, int quantidade, double preco, String categoria) {
        setNome(nome);
        setQuantidade(quantidade);
        setPreco(preco);
        setCategoria(categoria);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int valor) {
        id.set(valor);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNome() {
        return nome.get();
    }

    public void setNome(String valor) {
        nome.set(valor);
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade.get();
    }

    public void setQuantidade(int valor) {
        quantidade.set(valor);
    }

    public IntegerProperty quantidadeProperty() {
        return quantidade;
    }

    public double getPreco() {
        return preco.get();
    }

    public void setPreco(double valor) {
        preco.set(valor);
    }

    public DoubleProperty precoProperty() {
        return preco;
    }

    public String getCategoria() {
        return categoria.get();
    }

    public void setCategoria(String valor) {
        categoria.set(valor);
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

    /** Status do estoque (Normal, Baixo ou Esgotado). */
    public StatusEstoque getStatus() {
        return StatusEstoque.calcular(getQuantidade());
    }

    /** Valor total investido/parado nesse item do estoque (quantidade x preço unitário). */
    public double getValorTotal() {
        return getQuantidade() * getPreco();
    }
}
