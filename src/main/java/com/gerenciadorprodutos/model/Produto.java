package com.gerenciadorprodutos.model;

import javafx.beans.property.*;

/**
 * Entidade de dominio. Usa propriedades do JavaFX (em vez de campos simples)
 * porque a TableView da interface observa essas propriedades diretamente -
 * quando um valor muda, a celula da tabela se atualiza sozinha, sem precisar
 * recarregar a lista inteira.
 */
public class Produto {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final IntegerProperty quantidade = new SimpleIntegerProperty();
    private final DoubleProperty preco = new SimpleDoubleProperty();
    private final StringProperty categoria = new SimpleStringProperty();

    public Produto() {
    }

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

    /** Status sempre derivado da quantidade atual, nunca guardado "cru" no banco. */
    public StatusEstoque getStatus() {
        return StatusEstoque.calcular(getQuantidade());
    }

    /** Valor total investido/parado nesse item do estoque. */
    public double getValorTotal() {
        return getQuantidade() * getPreco();
    }
}
