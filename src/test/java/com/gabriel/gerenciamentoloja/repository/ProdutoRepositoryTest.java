package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.exception.ProdutoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoRepositoryTest {
    private ProdutoRepository repository;
    private Produto produto;

    @BeforeEach
    void configurar() {
        produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository = new ProdutoRepository();
    }

    @Test
    void deveCadastrarProduto() {
        repository.cadastrar(produto);
        Produto encontrado = repository.buscarPorId(produto.getId());
        assertSame(encontrado, produto);
    }

    @Test
    void deveRejeitarProdutoNulo() {
        assertThrows(IllegalArgumentException.class, () -> repository.cadastrar(null));
    }

    @Test
    void deveRejeitarIdDuplicado() {
        Produto produto1 = new Produto(1, "Mouse", new BigDecimal("59.90"), 5);
        repository.cadastrar(produto);
        assertThrows(IllegalArgumentException .class, () -> repository.cadastrar(produto1));

        Produto encontrado = repository.buscarPorId(produto.getId());
        assertSame(produto, encontrado);
    }

    @Test
    void deveBuscarProdutoPorId() {
        repository.cadastrar(produto);
        Produto encontrado = repository.buscarPorId(produto.getId());
        assertSame(encontrado, produto);
    }

    @Test
    void deveRejeitarProdutoNaoEncontrado() {
        repository.cadastrar(produto);
        ProdutoNaoEncontradoException excecao =  assertThrows(ProdutoNaoEncontradoException .class, () -> repository.buscarPorId(2));
        assertEquals("Produto não encontrado",excecao.getMessage());
    }

    @Test
    void deveListarTodosOsProdutos() {
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("89.90"), 5);
        Produto produto2 = new Produto(3, "Monitor", new BigDecimal("559.90"), 7);
        repository.cadastrar(produto);
        repository.cadastrar(produto1);
        repository.cadastrar(produto2);
        assertTrue(repository.listarTodos().contains(produto));
        assertTrue(repository.listarTodos().contains(produto1));
        assertTrue(repository.listarTodos().contains(produto2));
    }

    @Test
    void deveRemoverProdutoPorId() {
        repository.cadastrar(produto);
        repository.removerPorId(produto.getId());
        ProdutoNaoEncontradoException excecao = assertThrows(ProdutoNaoEncontradoException.class, () -> repository.buscarPorId(produto.getId()));
        assertEquals("Produto não encontrado",excecao.getMessage());
    }

    @Test
    void deveProtegerColecaoDeProdutos() {
        repository.cadastrar(produto);
        assertThrows(UnsupportedOperationException.class, () -> repository.listarTodos().clear());
        assertSame(produto, repository.buscarPorId(produto.getId()));
    }

    @Test
    void deveRejeitarRemocaoDeProdutoInexistente() {
        assertThrows(ProdutoNaoEncontradoException .class, () -> repository.removerPorId(999));
    }

}
