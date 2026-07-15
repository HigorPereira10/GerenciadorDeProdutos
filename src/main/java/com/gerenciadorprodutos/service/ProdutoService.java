package com.gerenciadorprodutos.service;

import com.gerenciadorprodutos.exception.ProdutoNaoEncontradoException;
import com.gerenciadorprodutos.exception.ValidacaoException;
import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.model.StatusEstoque;
import com.gerenciadorprodutos.repository.ProdutoRepository;

import java.util.List;

/**
 * Onde moram as regras de negocio. A interface (JavaFX) nunca fala direto
 * com o repositorio - sempre passa por aqui, entao validacao e calculos
 * ficam num lugar so, independente de quem os chama (tela hoje, uma API amanha).
 */
public class ProdutoService {

    private final ProdutoRepository repositorio;

    public ProdutoService(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Produto adicionar(String nome, int quantidade, double preco, String categoria) {
        validar(nome, quantidade, preco);
        return repositorio.salvar(new Produto(nome.trim(), quantidade, preco, categoria));
    }

    public void atualizar(int id, String nome, int quantidade, double preco, String categoria) {
        validar(nome, quantidade, preco);
        buscarPorIdOuFalhar(id);

        Produto produto = new Produto(nome.trim(), quantidade, preco, categoria);
        produto.setId(id);
        repositorio.atualizar(produto);
    }

    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        repositorio.excluir(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return nome == null || nome.isBlank()
                ? repositorio.listarTodos()
                : repositorio.buscarPorNome(nome.trim());
    }

    public List<Produto> listarTodos() {
        return repositorio.listarTodos();
    }

    /** Quantos itens diferentes estao com estoque baixo ou esgotado - usado nos cartoes do dashboard. */
    public long contarEstoqueBaixo(List<Produto> produtos) {
        return produtos.stream()
                .filter(p -> p.getStatus() != StatusEstoque.NORMAL)
                .count();
    }

    /** Soma de quantidade x preco de todos os produtos - o valor total parado em estoque. */
    public double calcularValorTotalEmEstoque(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(Produto::getValorTotal)
                .sum();
    }

    private Produto buscarPorIdOuFalhar(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    private void validar(String nome, int quantidade, double preco) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("O nome do produto nao pode ficar em branco.");
        }
        if (quantidade < 0) {
            throw new ValidacaoException("A quantidade nao pode ser negativa.");
        }
        if (preco < 0) {
            throw new ValidacaoException("O preco nao pode ser negativo.");
        }
    }
}
