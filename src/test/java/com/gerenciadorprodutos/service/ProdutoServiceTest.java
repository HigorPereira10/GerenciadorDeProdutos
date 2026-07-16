package com.gerenciadorprodutos.service;

import com.gerenciadorprodutos.exception.ProdutoNaoEncontradoException;
import com.gerenciadorprodutos.exception.ValidacaoException;
import com.gerenciadorprodutos.model.Produto;
import com.gerenciadorprodutos.model.StatusEstoque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as regras de negócio do ProdutoService de forma isolada, usando o
 * RepositorioEmMemoria (um "dublê" de teste) no lugar do SQLite de verdade.
 * Isso deixa os testes rápidos e focados apenas na lógica de validação e
 * cálculo, sem depender de banco de dados.
 */
class ProdutoServiceTest {

    private ProdutoService service;

    /** Executado antes de cada teste: cria um serviço novo com um repositório em memória "limpo". */
    @BeforeEach
    void configurar() {
        service = new ProdutoService(new RepositorioEmMemoria());
    }

    @Test
    void deveAdicionarProdutoComDadosValidos() {
        Produto produto = service.adicionar("Detergente Neutro", 20, 5.90, "Limpeza");

        assertTrue(produto.getId() > 0);
        assertEquals("Detergente Neutro", produto.getNome());
        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void naoDeveAdicionarProdutoComNomeEmBranco() {
        // "   " é uma string só com espaços - deve ser tratada como nome em branco.
        assertThrows(ValidacaoException.class, () -> service.adicionar("   ", 10, 5.0, "Limpeza"));
    }

    @Test
    void naoDeveAdicionarProdutoComQuantidadeNegativa() {
        assertThrows(ValidacaoException.class, () -> service.adicionar("Sabao", -1, 5.0, "Limpeza"));
    }

    @Test
    void naoDeveAdicionarProdutoComPrecoNegativo() {
        assertThrows(ValidacaoException.class, () -> service.adicionar("Sabao", 10, -0.01, "Limpeza"));
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        // 999 é um id que nunca foi cadastrado neste teste, então deve falhar.
        assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.atualizar(999, "Qualquer", 1, 1.0, "Categoria"));
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        assertThrows(ProdutoNaoEncontradoException.class, () -> service.excluir(999));
    }

    @Test
    void statusDeveSerCalculadoConformeAQuantidade() {
        // Um produto para cada faixa de status: 0 unidades (esgotado),
        // poucas unidades (baixo) e bastante unidades (normal).
        Produto esgotado = service.adicionar("Item A", 0, 1.0, "Categoria");
        Produto baixo = service.adicionar("Item B", 5, 1.0, "Categoria");
        Produto normal = service.adicionar("Item C", 50, 1.0, "Categoria");

        assertEquals(StatusEstoque.ESGOTADO, esgotado.getStatus());
        assertEquals(StatusEstoque.BAIXO, baixo.getStatus());
        assertEquals(StatusEstoque.NORMAL, normal.getStatus());
    }

    @Test
    void deveContarApenasItensComEstoqueBaixoOuEsgotado() {
        service.adicionar("Item A", 0, 1.0, "Categoria");
        service.adicionar("Item B", 5, 1.0, "Categoria");
        service.adicionar("Item C", 50, 1.0, "Categoria");

        List<Produto> todos = service.listarTodos();

        // Dos 3 produtos, só o "Item C" está com estoque normal - por isso o
        // resultado esperado é 2 (esgotado + baixo).
        assertEquals(2, service.contarEstoqueBaixo(todos));
    }

    @Test
    void deveCalcularValorTotalEmEstoqueCorretamente() {
        service.adicionar("Item A", 10, 2.0, "Categoria");
        service.adicionar("Item B", 5, 3.0, "Categoria");

        double valorTotal = service.calcularValorTotalEmEstoque(service.listarTodos());

        // (10 x 2.0) + (5 x 3.0) = 20 + 15 = 35.
        assertEquals(35.0, valorTotal, 0.0001);
    }

    @Test
    void buscaPorNomeDeveIgnorarCaixaEBuscarPorTrecho() {
        service.adicionar("Detergente Neutro", 10, 5.0, "Limpeza");
        service.adicionar("Sabao em Po", 10, 5.0, "Limpeza");

        // Busca em minúsculas ("deter") por um trecho do nome, que está em
        // maiúsculas/minúsculas misturadas ("Detergente") - deve encontrar mesmo assim.
        List<Produto> resultado = service.buscarPorNome("deter");

        assertEquals(1, resultado.size());
        assertEquals("Detergente Neutro", resultado.get(0).getNome());
    }
}
