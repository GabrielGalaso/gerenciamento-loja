package com.gabriel.gerenciamentoloja.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {
    private Produto produto;

    @Test
    void deveCriarProdutoValido(){
        Produto criado = assertDoesNotThrow(() -> produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10));
        assertEquals(10, criado.getQuantidade());
        assertEquals(1, criado.getId());
        assertEquals("Teclado", criado.getNome());
        assertEquals(new BigDecimal("159.90"), criado.getPreco());
    }

    @Test
    void deveRejeitarNomeVazio(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1,"", new BigDecimal("159.90"), 10));
    }

    @Test
    void deveRejeitarPrecoInvalido(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1,"Teclado", new BigDecimal("-159.90"), 10));
    }

    @Test
    void deveRejeitarEstoqueNegativo(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1,"Teclado", new BigDecimal("159.90"), -5));
    }

    @Test
    void deveAdicionarAoEstoque(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        produto.adicionarAoEstoque(5);
        assertEquals(15, produto.getQuantidade());
    }

    @Test
    void deveRejeitarPrecoZero(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1,"Teclado", BigDecimal.ZERO, 10));
    }

    @Test
    void deveRemoverEspacosDoNome(){
        produto = new Produto(1," Teclado ", new BigDecimal("159.90"), 10);
        assertEquals("Teclado", produto.getNome());
    }

    @Test
    void deveRejeitarAdicaoDeEstoqueInvalida(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        assertThrows(IllegalArgumentException.class, () -> produto.adicionarAoEstoque(-5));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRetirarDoEstoque(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        produto.retirarDoEstoque(5);
        assertEquals(5, produto.getQuantidade());
    }

    @Test
    void deveRejeitarRetiradaComEstoqueInsuficiente(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        assertThrows(IllegalArgumentException.class, () -> produto.retirarDoEstoque(11));
        assertEquals(10, produto.getQuantidade());
    }


    @Test
    void deveRejeitarNomeApenasComEspacos(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1," ", new BigDecimal("159.90"), 10));
    }

    @Test
    void deveRejeitarIdInvalido(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(-1,"Teclado", new BigDecimal("159.90"), 10));
    }

    @Test
    void deveRejeitarPrecoNulo(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(1,"Teclado", null, 10));
    }

    @Test
    void deveRejeitarAdicaoDeEstoqueZero(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        assertThrows(IllegalArgumentException.class,() -> produto.adicionarAoEstoque(0));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRejeitarRetiradaDeQuantidadeInvalida(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        assertThrows(IllegalArgumentException.class, () -> produto.retirarDoEstoque(0));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void devePermitirRetiradaDeEstoqueExato(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        produto.retirarDoEstoque(10);
        assertEquals(0, produto.getQuantidade());
    }

    @Test
    void deveRejeitarIdZero(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(0,"Teclado", new BigDecimal("159.90"), 10));
    }

    @Test
    void deveRejeitarRetiradaNegativa(){
        produto = new Produto(1,"Teclado", new BigDecimal("159.90"), 10);
        assertThrows(IllegalArgumentException.class, () ->produto.retirarDoEstoque(-10));
        assertEquals(10, produto.getQuantidade());
    }

}
