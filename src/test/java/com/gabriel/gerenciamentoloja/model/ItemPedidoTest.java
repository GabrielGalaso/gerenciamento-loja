package com.gabriel.gerenciamentoloja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ItemPedidoTest {
    private Produto produto;
    private ItemPedido itemPedido;

    @BeforeEach
    void configurar() {
        produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        itemPedido = new ItemPedido(produto, 3);
    }

    @Test
    void deveCriarItemPedidoValido(){
        assertEquals("Teclado", itemPedido.getProduto().getNome());
        assertEquals(3, itemPedido.getQuantidade());
        assertEquals(new BigDecimal("159.90"), itemPedido.getPrecoUnitario());
    }

    @Test
    void deveRejeitarProdutoNulo(){
        assertThrows(IllegalArgumentException.class, () -> itemPedido = new ItemPedido(null, 3));
    }

    @Test
    void deveRejeitarQuantidadeInvalida(){
        assertThrows(IllegalArgumentException.class, () -> itemPedido = new ItemPedido(produto, -3));
    }

    @Test
    void deveRejeitarQuantidadeZero(){
        assertThrows(IllegalArgumentException.class, () -> itemPedido = new ItemPedido(produto, 0));
    }

    @Test
    void deveCalcularSubTotal(){
        assertEquals(new BigDecimal("479.70"), itemPedido.calcularSubtotal());
    }

    @Test
    void devePreservarPrecoUnitarioOriginal(){
        assertDoesNotThrow( () -> produto.setPreco(new BigDecimal("199.90")));
        assertEquals(new BigDecimal("159.90"), itemPedido.getPrecoUnitario());
        assertEquals(new BigDecimal("199.90"), produto.getPreco());
        assertEquals(new BigDecimal("479.70"), itemPedido.calcularSubtotal());
    }

}
