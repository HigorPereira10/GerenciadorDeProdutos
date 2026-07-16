package com.gerenciadorprodutos.service;

import com.gerenciadorprodutos.exception.ProdutoNaoEncontradoException;
import com.gerenciadorprodutos.exception.ValidacaoException;
import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.model.StatusEstoque;
import com.gerenciadorprodutos.repository.ProdutoRepository;

import java.util.List;

/**
 * Regras de negócio da aplicação (validações, cálculos etc).
 *
 * A interface gráfica (JavaFX) nunca fala direto com o repositório - sempre
 * passa por aqui. Assim, validação e cálculos ficam concentrados em um único
 * lugar, independente de quem os chama (a tela hoje, uma API no futuro, etc).
 */
public class ProdutoService {

    private final ProdutoRepository repositorio;

    public ProdutoService(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** Valida os dados e cadastra um produto novo, devolvendo-o já com o id gerado pelo banco. */
    public Produto adicionar(String nome, int quantidade, double preco, String categoria) {
        validar(nome, quantidade, preco);
        return repositorio.salvar(new Produto(nome.trim(), quantidade, preco, categoria));
    }

    /** Valida os dados e atualiza um produto existente. Lança ProdutoNaoEncontradoException se o id informado não existir. */
    public void atualizar(int id, String nome, int quantidade, double preco, String categoria) {
        validar(nome, quantidade, preco);
        buscarPorIdOuFalhar(id);

        Produto produto = new Produto(nome.trim(), quantidade, preco, categoria);
        produto.setId(id);
        repositorio.atualizar(produto);
    }

    /** Remove um produto existente. Lança ProdutoNaoEncontradoException se o id não existir. */
    public void excluir(int id) {
        buscarPorIdOuFalhar(id);
        repositorio.excluir(id);
    }

    /**
     * Busca produtos pelo nome. Se o texto de busca estiver vazio/nulo, devolve todos os produtos em vez de fazer uma busca "vazia". */
    public List<Produto> buscarPorNome(String nome) {
        return nome == null || nome.isBlank()
                ? repositorio.listarTodos()
                : repositorio.buscarPorNome(nome.trim());
    }

    /** Lista todos os produtos cadastrados, sem nenhum filtro. */
    public List<Produto> listarTodos() {
        return repositorio.listarTodos();
    }

    /** Quantos itens diferentes estão com estoque baixo ou esgotado - usado nos cartões do dashboard. */
    public long contarEstoqueBaixo(List<Produto> produtos) {
        return produtos.stream()
                .filter(p -> p.getStatus() != StatusEstoque.NORMAL)
                .count();
    }

    /** Soma de quantidade x preço de todos os produtos - o valor total parado em estoque. */
    public double calcularValorTotalEmEstoque(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(Produto::getValorTotal)
                .sum();
    }

    /** Busca o produto pelo id ou lança ProdutoNaoEncontradoException caso ele não exista. */
    private Produto buscarPorIdOuFalhar(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    /**
     * Aplica as regras básicas de negócio antes de salvar/atualizar um produto:
     * nome não pode ficar em branco; quantidade e preço não podem ser negativos.
     * Caso alguma regra seja violada, lança ValidacaoException com uma mensagem amigável, que pode ser exibida direto na tela.
     */
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
