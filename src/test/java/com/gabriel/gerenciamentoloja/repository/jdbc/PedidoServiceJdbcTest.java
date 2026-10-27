package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.service.PedidoServiceJdbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class PedidoServiceJdbcTest {

    private PedidoServiceJdbc pedidoServiceJdbc;
    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void configurar() throws SQLException {
        pedidoServiceJdbc = new PedidoServiceJdbc(new PedidoRepositoryJdbc());
        cliente = new Cliente("pedro@gmail.com", 5, "Pedro");
        pedido = new Pedido(1, cliente);
    }

    @Test
    void deveRejeitarRepositorioNulo() {
        assertThrows(IllegalArgumentException.class, () -> new PedidoServiceJdbc(null));
    }

    @Test
    void deveRejeitaPedidoNulo() {
        assertThrows(IllegalArgumentException.class, () -> pedidoServiceJdbc.finalizarPedido(null));
    }

    @Test
    void deveRejeitarPedidoVazio() {
        assertThrows(IllegalArgumentException.class, () -> pedidoServiceJdbc.finalizarPedido(pedido));
    }

}
