package com.gabriel.gerenciamentoloja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {
    private Cliente cliente;
    private Pedido pedido;
    private Produto produto;
    private ItemPedido itemPedido;

    @BeforeEach
    void configurar() {
        cliente = new Cliente("pedro@gmail.com", 1, "Pedro");
        pedido = new Pedido(1, cliente);
        produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        itemPedido = new ItemPedido(produto, 3);
    }

    @Test
    void deveCriarPedidoValido(){
        assertEquals(1, pedido.getId());
        assertSame(cliente, pedido.getCliente());
        assertTrue(pedido.getItens().isEmpty());
    }

    @Test
    void deveRejeitarIdInvalido(){
        assertThrows(IllegalArgumentException.class, () -> pedido = new Pedido(-1, cliente));
    }

    @Test
    void deveRejeitarIdZero(){
        assertThrows(IllegalArgumentException.class, () -> pedido = new Pedido(0, cliente));
    }

    @Test
    void deveRejeitarClienteNulo(){
        assertThrows(IllegalArgumentException.class, () -> pedido = new Pedido(1, null));
    }

    @Test
    void deveAdicionarItemAoPedido(){
        pedido.adicionarItem(itemPedido);
        assertTrue(pedido.getItens().contains(itemPedido));
    }

    @Test
    void deveRejeitarItemNulo(){
        assertThrows(IllegalArgumentException.class, () -> pedido.adicionarItem(null));
        assertTrue(pedido.getItens().isEmpty());
    }

    @Test
    void deveCalcularTotalDoPedido(){
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("89.90"), 5);
        ItemPedido itemPedido1 = new ItemPedido(produto1, 2);
        pedido.adicionarItem(itemPedido);
        pedido.adicionarItem(itemPedido1);
        assertEquals(new BigDecimal("659.50"), pedido.calcularTotal());
    }

    @Test
    void deveProtegerListaDeItens(){
        pedido.adicionarItem(itemPedido);
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItens().clear());
        assertEquals(1, pedido.getItens().size());
        assertSame(itemPedido, pedido.getItens().get(0));
    }

    @Test
    void devePreservarCopiaAnteriorDaLista() {
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("89.90"), 5);
        ItemPedido itemPedido1 = new ItemPedido(produto1, 2);

        pedido.adicionarItem(itemPedido);
        List<ItemPedido> itens = pedido.getItens();
        pedido.adicionarItem(itemPedido1);

        assertEquals(1, itens.size());
        assertEquals(2, pedido.getItens().size());

        assertSame(itemPedido, itens.get(0));
        assertSame(itemPedido1, pedido.getItens().get(1));
    }
}
