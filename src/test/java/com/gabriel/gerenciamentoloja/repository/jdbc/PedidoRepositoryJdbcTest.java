package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.PedidoJaFinalizadoException;
import com.gabriel.gerenciamentoloja.exception.PedidoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoRepositoryJdbcTest {

    private boolean clienteCriado;
    private boolean tecladoCriado;
    private boolean mouseCriado;
    private boolean pedidoTentado;

    private Pedido pedido, pedido1;
    private Cliente cliente;
    private Produto produto, produto1;
    private ProdutoRepositoryJdbc produtoRepositoryJdbc;
    private PedidoRepositoryJdbc pedidoRepositoryJdbc;
    private ClienteRepositoryJdbc clienteRepositoryJdbc;


    @BeforeEach
    void configurar() throws SQLException {

        String sql = "SELECT COUNT(*) FROM pedidos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());
        }

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            ps.setInt(1, 910010);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1), "O ID 910010 já está ocupado no banco de testes");
            }
        }

        pedidoRepositoryJdbc = new PedidoRepositoryJdbc();

        clienteRepositoryJdbc = new ClienteRepositoryJdbc();
        cliente = new Cliente("teste.pedido.910010@exemplo.test", 910010, "Pedro");
        clienteRepositoryJdbc.cadastrar(cliente);
        clienteCriado = true;

        produtoRepositoryJdbc = new ProdutoRepositoryJdbc();
        produto = new Produto(910010, "Teclado", new BigDecimal("150.00"),10);
        produto1 = new Produto(910012, "Mouse", new BigDecimal("80.00"),20);

        produtoRepositoryJdbc.cadastrar(produto);
        tecladoCriado = true;
        produtoRepositoryJdbc.cadastrar(produto1);
        mouseCriado = true;
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
            if (mouseCriado) {
                try (PreparedStatement ps = conexao.prepareStatement(sqlProdutos)) {
                    ps.setInt(1, produto1.getId());
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
    void deveCadastrarPedidoComItensEAtualizarEstoque() throws SQLException {

        pedido = new Pedido(910010, cliente);

        ItemPedido teclado = new ItemPedido(produto, 3);
        ItemPedido mouse = new ItemPedido(produto1, 5);

        pedido.adicionarItem(teclado);
        pedido.adicionarItem(mouse);

        pedidoTentado = true;
        assertDoesNotThrow(() -> pedidoRepositoryJdbc.cadastrar(pedido));

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

                assertEquals(2, quantidadeItens);
            }
        }

        String sqlEstoque = "SELECT quantidade FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {

            ps.setInt(1, produto.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(7, rs.getInt("quantidade"));
            }

            ps.setInt(1, produto1.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(15, rs.getInt("quantidade"));
            }
        }

    }

    @Test
    void deveDesfazerTransacaoQuandoEstoqueForInsuficiente() throws SQLException {
        pedido = new Pedido(910010, cliente);

        ItemPedido mouse = new ItemPedido(produto1, 2);
        ItemPedido teclado = new ItemPedido(produto, 50);

        pedido.adicionarItem(mouse);
        pedido.adicionarItem(teclado);

        pedidoTentado = true;
        IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> pedidoRepositoryJdbc.cadastrar(pedido));
        assertTrue(excecao.getMessage().contains("estoque insuficiente"));

        String sqlEstoque = "SELECT quantidade FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {

            ps.setInt(1, produto1.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(20, rs.getInt("quantidade"));
            }

            ps.setInt(1, produto.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(10, rs.getInt("quantidade"));
            }

        }

        String sql = "SELECT COUNT(*) FROM pedidos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadePedidos = rs.getInt(1);

                assertEquals(0, quantidadePedidos);
            }
        }

        String sqlItens = "SELECT COUNT(*) FROM itens_pedido WHERE pedido_id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlItens)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadeItens = rs.getInt(1);

                assertEquals(0, quantidadeItens);
            }
        }
    }

    @Test
    void naoDeveCadastrarPedidoComIdDuplicado() throws SQLException {
        pedido = new Pedido(910010, cliente);
        ItemPedido teclado = new ItemPedido(produto, 3);
        pedido.adicionarItem(teclado);
        pedidoTentado = true;
        pedidoRepositoryJdbc.cadastrar(pedido);

        pedido1 = new Pedido(910010, cliente);
        pedido1.adicionarItem(teclado);

        assertThrows(PedidoJaFinalizadoException.class, () -> pedidoRepositoryJdbc.cadastrar(pedido1));

        String sqlEstoque = "SELECT quantidade FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {

            ps.setInt(1, produto1.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(20, rs.getInt("quantidade"));
            }

            ps.setInt(1, produto.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(7, rs.getInt("quantidade"));
            }

        }

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

    }

    @Test
    void deveBuscarPedidoPorIdComCliente() {
        pedido = new Pedido(910010, cliente);
        ItemPedido teclado = new ItemPedido(produto, 2);
        pedido.adicionarItem(teclado);
        pedidoTentado = true;
        pedidoRepositoryJdbc.cadastrar(pedido);

        Pedido encontrado = pedidoRepositoryJdbc.buscarPorId(pedido.getId());
        assertEquals(pedido.getId(), encontrado.getId());
        assertEquals(cliente.getId(), encontrado.getCliente().getId());
        assertEquals(cliente.getNome(), encontrado.getCliente().getNome());
        assertEquals(cliente.getEmail(), encontrado.getCliente().getEmail());

        assertEquals(1, encontrado.getItens().size());
        ItemPedido itemEncontrado = encontrado.getItens().get(0);
        assertEquals(produto.getId(), itemEncontrado.getProduto().getId());
        assertEquals(2, itemEncontrado.getQuantidade());
        assertEquals(new BigDecimal("150.00"), encontrado.getItens().get(0).getPrecoUnitario());
        assertEquals(new BigDecimal("300.00"), itemEncontrado.calcularSubtotal());
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistir() {
        assertThrows(PedidoNaoEncontradoException.class, () -> pedidoRepositoryJdbc.buscarPorId(543));
    }

    @Test
    void devePreservarPrecoHistoricoDoItem() {
        Produto produtoAtualizado = new Produto(910013, "Teclado", new BigDecimal("200.00"), 10);
        ItemPedido itemPedido = new ItemPedido(produtoAtualizado, 2, new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), itemPedido.getPrecoUnitario());
        assertEquals(new BigDecimal("300.00"), itemPedido.calcularSubtotal());
    }

    @Test
    void deveManterPrecoHistoricoAoBuscarPedido() throws SQLException{
        pedido = new Pedido(910010, cliente);
        ItemPedido teclado = new ItemPedido(produto, 2);
        pedido.adicionarItem(teclado);
        pedidoTentado = true;
        pedidoRepositoryJdbc.cadastrar(pedido);

        String sql = """
            UPDATE produtos
            SET preco = ?
            WHERE id = ?
            """;

        try (Connection conexao = ConexaoFactory.abrirConexao()) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            try (PreparedStatement ps = conexao.prepareStatement(sql)) {

                ps.setBigDecimal(1, new BigDecimal("200.00"));
                ps.setInt(2, produto.getId());

                assertEquals(1, ps.executeUpdate());
            }
        }

        Pedido encontrado = pedidoRepositoryJdbc.buscarPorId(pedido.getId());

        assertEquals(new BigDecimal("200.00"), encontrado.getItens().get(0).getProduto().getPreco());
        assertEquals(new BigDecimal("150.00"), encontrado.getItens().get(0).getPrecoUnitario());
        assertEquals(new BigDecimal("300.00"), encontrado.calcularTotal());
    }

    @Test
    void deveListarTodosOsPedidos(){
        pedido = new Pedido(910010, cliente);

        ItemPedido mouse = new ItemPedido(produto1, 2);
        ItemPedido teclado = new ItemPedido(produto, 3);

        pedido.adicionarItem(mouse);
        pedido.adicionarItem(teclado);

        pedidoTentado = true;
        assertDoesNotThrow(() -> pedidoRepositoryJdbc.cadastrar(pedido));

        Collection<Pedido> pedidosEncontrados = pedidoRepositoryJdbc.listarTodos();

        Pedido encontrado = pedidosEncontrados.stream()
                .filter(p -> p.getId() == pedido.getId())
                .findFirst()
                .orElseThrow();

        assertEquals(cliente.getId(), encontrado.getCliente().getId());
        assertEquals(2, encontrado.getItens().size());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverPedidos(){
        Collection<Pedido> pedidos = pedidoRepositoryJdbc.listarTodos();
        assertTrue(pedidos.isEmpty());
    }

    @Test
    void deveRemoverPedidoComSeusItens() throws SQLException {
        pedido = new Pedido(910010, cliente);

        ItemPedido mouse = new ItemPedido(produto1, 2);
        ItemPedido teclado = new ItemPedido(produto, 3);

        pedido.adicionarItem(mouse);
        pedido.adicionarItem(teclado);

        pedidoTentado = true;
        pedidoRepositoryJdbc.cadastrar(pedido);
        assertDoesNotThrow(() -> pedidoRepositoryJdbc.removerPorId(pedido.getId()));

        String sqlEstoque = "SELECT quantidade FROM produtos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {

            ps.setInt(1, produto1.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(18, rs.getInt("quantidade"));
            }

            ps.setInt(1, produto.getId());

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(7, rs.getInt("quantidade"));
            }

        }

        String sql = "SELECT COUNT(*) FROM pedidos WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadePedidos = rs.getInt(1);

                assertEquals(0, quantidadePedidos);
            }
        }

        String sqlItens = "SELECT COUNT(*) FROM itens_pedido WHERE pedido_id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao();
             PreparedStatement ps = conexao.prepareStatement(sqlItens)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                int quantidadeItens = rs.getInt(1);

                assertEquals(0, quantidadeItens);
            }
        }
    }
    @Test
    void deveLancarExcecaoAoRemoverPedidoInexistente() {
        assertThrows(PedidoNaoEncontradoException.class, () -> pedidoRepositoryJdbc.removerPorId(999999));
    }
    @Test
    void deveRejeitarIdInvalidoAoRemoverPedido() {
        assertThrows(IllegalArgumentException.class, () -> pedidoRepositoryJdbc.removerPorId(null));

        assertThrows(IllegalArgumentException.class, () -> pedidoRepositoryJdbc.removerPorId(0));

        assertThrows(IllegalArgumentException.class, () -> pedidoRepositoryJdbc.removerPorId(-1));
    }
}
