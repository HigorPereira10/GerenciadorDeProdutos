package com.gerenciadorprodutos.repository;

import com.gerenciadorprodutos.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o repositorio contra um SQLite de verdade, gravado em um arquivo
 * temporario (apagado pelo JUnit ao final) - garante que o SQL escrito
 * a mao realmente funciona, sem sujar o estoque.db usado pela aplicacao.
 */
class SqliteProdutoRepositoryTest {

    private ProdutoRepository repositorio;

    @BeforeEach
    void configurar(@TempDir Path pastaTemporaria) {
        String urlDeTeste = "jdbc:sqlite:" + pastaTemporaria.resolve("teste.db");
        repositorio = new SqliteProdutoRepository(urlDeTeste);
    }

    @Test
    void deveSalvarEGerarIdAutomaticamente() {
        Produto salvo = repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        assertTrue(salvo.getId() > 0);
    }

    @Test
    void deveEncontrarProdutoSalvoPeloId() {
        Produto salvo = repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        Optional<Produto> encontrado = repositorio.buscarPorId(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Detergente", encontrado.get().getNome());
    }

    @Test
    void deveAtualizarOsDadosDeUmProdutoExistente() {
        Produto salvo = repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        salvo.setQuantidade(3);
        salvo.setPreco(6.0);
        repositorio.atualizar(salvo);

        Produto atualizado = repositorio.buscarPorId(salvo.getId()).orElseThrow();
        assertEquals(3, atualizado.getQuantidade());
        assertEquals(6.0, atualizado.getPreco());
    }

    @Test
    void deveExcluirProdutoDoBanco() {
        Produto salvo = repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        repositorio.excluir(salvo.getId());

        assertTrue(repositorio.buscarPorId(salvo.getId()).isEmpty());
    }

    @Test
    void deveListarTodosOsProdutosEmOrdemAlfabetica() {
        repositorio.salvar(new Produto("Sabao em Po", 10, 5.5, "Limpeza"));
        repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        List<Produto> todos = repositorio.listarTodos();

        assertEquals(2, todos.size());
        assertEquals("Detergente", todos.get(0).getNome());
        assertEquals("Sabao em Po", todos.get(1).getNome());
    }

    @Test
    void deveBuscarProdutosPorTrechoDoNome() {
        repositorio.salvar(new Produto("Detergente Neutro", 10, 5.5, "Limpeza"));
        repositorio.salvar(new Produto("Sabao em Po", 10, 5.5, "Limpeza"));

        List<Produto> encontrados = repositorio.buscarPorNome("deter");

        assertEquals(1, encontrados.size());
    }
}
