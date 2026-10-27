package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.jdbc.ClienteRepositoryJdbc;
import com.gabriel.gerenciamentoloja.repository.jdbc.PedidoRepositoryJdbc;
import com.gabriel.gerenciamentoloja.repository.jdbc.ProdutoRepositoryJdbc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PedidoServiceJdbcIntegracaoTest {

    private boolean clienteCriado;
    private boolean tecladoCriado;
    private boolean pedidoTentado;

    private Pedido pedido;
    private Cliente cliente;
    private Produto produto;
    private ProdutoRepositoryJdbc produtoRepositoryJdbc;
    private PedidoRepositoryJdbc pedidoRepositoryJdbc;
    private ClienteRepositoryJdbc clienteRepositoryJdbc;
    private PedidoServiceJdbc pedidoServiceJdbc;


    @BeforeEach
    void configurar() throws SQLException {

        String sql = "SELECT COUNT(*) FROM pedidos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());
        }

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            ps.setInt(1, 920010);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1), "O ID 920010 já está ocupado no banco de testes");
            }
        }

        pedidoRepositoryJdbc = new PedidoRepositoryJdbc();
        pedidoServiceJdbc = new PedidoServiceJdbc(pedidoRepositoryJdbc);

        clienteRepositoryJdbc = new ClienteRepositoryJdbc();
        cliente = new Cliente("teste.service.920010@exemplo.test", 920010, "Pedro");
        clienteRepositoryJdbc.cadastrar(cliente);
        clienteCriado = true;

        produtoRepositoryJdbc = new ProdutoRepositoryJdbc();
        produto = new Produto(920011, "Teclado", new BigDecimal("150.00"),10);

        produtoRepositoryJdbc.cadastrar(produto);
        tecladoCriado = true;
    }

    @AfterEach
    void limpar() throws SQLException {

        String sqlItens = "DELETE FROM itens_pedido WHERE pedido_id = ?";
        String sqlPedidos = "DELETE FROM pedidos WHERE id = ?";
        String sqlProdutos = "DELETE FROM produtos WHERE id = ?";
        String sqlClientes = "DELETE FROM clientes WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao()) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            if (pedidoTentado) {
                try (PreparedStatement ps = conexao.prepareStatement(sqlItens)) {
                    ps.setInt(1, pedido.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conexao.prepareStatement(sqlPedidos)) {
                    ps.setInt(1, pedido.getId());
                    ps.executeUpdate();
                }
            }
            if (tecladoCriado) {
                try (PreparedStatement ps = conexao.prepareStatement(sqlProdutos)) {
                    ps.setInt(1, produto.getId());
                    ps.executeUpdate();
                }
            }
            if (clienteCriado) {
                try (PreparedStatement ps = conexao.prepareStatement(sqlClientes)) {
                    ps.setInt(1, cliente.getId());
                    ps.executeUpdate();
                }
            }

        }
    }

    @Test
    void deveFinalizarPedidoEAtualizarEstoque() throws SQLException {
        pedido = new Pedido(920010, cliente);
        ItemPedido itemPedido = new ItemPedido(produto, 2);
        pedido.adicionarItem(itemPedido);
        pedidoTentado = true;
        pedidoServiceJdbc.finalizarPedido(pedido);

        String sql = "SELECT COUNT(*) FROM pedidos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadePedidos = rs.getInt(1);

                assertEquals(1, quantidadePedidos);
            }
        }

        String sqlItens = "SELECT COUNT(*) FROM itens_pedido WHERE pedido_id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlItens)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadeItens = rs.getInt(1);

                assertEquals(1, quantidadeItens);
            }
        }

        String sqlEstoque = "SELECT quantidade FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {

            ps.setInt(1, produto.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(8, rs.getInt("quantidade"));
            }

        }

    }
}
