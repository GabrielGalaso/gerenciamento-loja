package com.gabriel.gerenciamentoloja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {
    private Produto produto;

    @BeforeEach
    void configurar() {
        produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
    }

    @Test
    void deveCriarProdutoValido(){
        assertEquals(10, produto.getQuantidade());
        assertEquals(1, produto.getId());
        assertEquals("Teclado", produto.getNome());
        assertEquals(new BigDecimal("159.90"), produto.getPreco());
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
        assertThrows(IllegalArgumentException.class, () -> produto.adicionarAoEstoque(-5));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRetirarDoEstoque(){
        produto.retirarDoEstoque(5);
        assertEquals(5, produto.getQuantidade());
    }

    @Test
    void deveRejeitarRetiradaComEstoqueInsuficiente(){
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
        assertThrows(IllegalArgumentException.class,() -> produto.adicionarAoEstoque(0));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRejeitarRetiradaDeQuantidadeInvalida(){
        assertThrows(IllegalArgumentException.class, () -> produto.retirarDoEstoque(0));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void devePermitirRetiradaDeEstoqueExato(){
        produto.retirarDoEstoque(10);
        assertEquals(0, produto.getQuantidade());
    }

    @Test
    void deveRejeitarIdZero(){
        assertThrows(IllegalArgumentException.class, () -> produto = new Produto(0,"Teclado", new BigDecimal("159.90"), 10));
    }

    @Test
    void deveRejeitarRetiradaNegativa(){
        assertThrows(IllegalArgumentException.class, () ->produto.retirarDoEstoque(-10));
        assertEquals(10, produto.getQuantidade());
    }

}
