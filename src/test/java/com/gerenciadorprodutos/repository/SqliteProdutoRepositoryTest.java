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
 * Testa o SqliteProdutoRepository contra um SQLite de verdade, gravado em um
 * arquivo temporário (apagado automaticamente pelo JUnit ao final de cada
 * teste, graças à anotação @TempDir) - isso garante que o SQL escrito à mão
 * realmente funciona, sem nunca sujar o estoque.db usado pela aplicação.
 *
 * Cada teste segue o padrão "given/when/then" (organizar-agir-verificar):
 * primeiro prepara o cenário, depois executa a ação sendo testada, e por
 * fim confere (assert) se o resultado é o esperado.
 */
class SqliteProdutoRepositoryTest {

    private ProdutoRepository repositorio;

    /**
     * Executado antes de cada teste (@BeforeEach): cria um repositório novo apontando para um banco SQLite temporário,
     * garantindo que os testes não interfiram uns nos outros.
     */
    @BeforeEach
    void configurar(@TempDir Path pastaTemporaria) {
        String urlDeTeste = "jdbc:sqlite:" + pastaTemporaria.resolve("teste.db");
        repositorio = new SqliteProdutoRepository(urlDeTeste);
    }

    @Test
    void deveSalvarEGerarIdAutomaticamente() {
        Produto salvo = repositorio.salvar(new Produto("Detergente", 10, 5.5, "Limpeza"));

        // O SQLite gera o id automaticamente (AUTOINCREMENT), então basta conferir que ele veio maior que zero.
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

        // Busca o produto de novo no banco para confirmar que os novos valores realmente foram gravados (e não só alterados em memória).
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
        // Salva de propósito fora de ordem alfabética, para confirmar que
        // o próprio banco (ORDER BY nome) é quem ordena, não a ordem de inserção.
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

        // Busca por um trecho ("deter"), não pelo nome completo, para confirmar
        // que o LIKE do SQL está configurado para buscar em qualquer posição do nome.
        List<Produto> encontrados = repositorio.buscarPorNome("deter");

        assertEquals(1, encontrados.size());
    }
}
