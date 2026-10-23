
package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.ProdutoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoRepositoryJdbcTest {

    private ProdutoRepositoryJdbc produtoRepositoryJdbc;
    private Produto produto;
    private boolean cadastradoNesteTeste;

    @BeforeEach
    void configurar() throws SQLException {
        produto = new Produto(900001, "Teclado", new BigDecimal("159.90"), 10);
        produtoRepositoryJdbc = new ProdutoRepositoryJdbc();
        cadastradoNesteTeste = false;
        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());
        }
    }

    @AfterEach
    void limpar() throws SQLException {
        if (!cadastradoNesteTeste) {
            return;
        }
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao()) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setInt(1, produto.getId());
                ps.executeUpdate();
            }
        }
    }

    @Test
    void deveConectarAoBancoDeTeste() throws SQLException {
        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            assertTrue(conexao.isValid(2));
            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());
        }
    }

    @Test
    void deveCadastrarProdutoNoBanco() {
        produtoRepositoryJdbc.cadastrar(produto);
        cadastradoNesteTeste = true;

        Produto encontrado = produtoRepositoryJdbc.buscarPorId(produto.getId());

        assertEquals(produto.getId(), encontrado.getId());
        assertEquals(produto.getNome(), encontrado.getNome());
        assertEquals(produto.getPreco(), encontrado.getPreco());
        assertEquals(produto.getQuantidade(), encontrado.getQuantidade());
    }

    @Test
    void deveRejeitarCadastroDuplicado() {
        produtoRepositoryJdbc.cadastrar(produto);
        cadastradoNesteTeste = true;

        IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> produtoRepositoryJdbc.cadastrar(produto));

        SQLException causa = assertInstanceOf(SQLException.class, excecao.getCause());

        assertEquals(1062, causa.getErrorCode());
    }

    @Test
    void deveRejeitarBuscaDeProdutoInexistente() {
        ProdutoNaoEncontradoException excecao =  assertThrows(ProdutoNaoEncontradoException.class, () -> produtoRepositoryJdbc.buscarPorId(999999));
        assertEquals("Produto não encontrado",excecao.getMessage());
    }

    @Test
    void deveListarProdutosDoBanco() {
        produtoRepositoryJdbc.cadastrar(produto);
        cadastradoNesteTeste = true;
        Collection<Produto> encontrados = produtoRepositoryJdbc.listarTodos();
        assertTrue(encontrados.stream().anyMatch(p -> p.getId() == produto.getId()));

    }

    @Test
    void deveRemoverProdutoDoBanco() {
        produtoRepositoryJdbc.cadastrar(produto);
        cadastradoNesteTeste = true;
        produtoRepositoryJdbc.removerPorId(produto.getId());
        ProdutoNaoEncontradoException excecao = assertThrows(ProdutoNaoEncontradoException.class, () -> produtoRepositoryJdbc.buscarPorId(produto.getId()));
        assertEquals("Produto não encontrado",excecao.getMessage());

    }

    @Test
    void deveRejeitarRemocaoDeProdutoInexistente() {
        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoRepositoryJdbc.removerPorId(produto.getId()));
    }


}
