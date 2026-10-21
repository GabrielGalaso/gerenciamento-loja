package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class RelatorioProdutoServiceTest {

    private RelatorioProdutoService relatorio;
    private ProdutoRepository repository;

    @BeforeEach
    void configurar() {
        repository = new ProdutoRepository();
        relatorio = new RelatorioProdutoService(repository);
    }
    private void cadastrarProdutosDeExemplo(Produto produto, Produto produto1) {
        repository.cadastrar(produto);
        repository.cadastrar(produto1);
    }

    @Test
    void deveListarProdutosComEstoqueBaixo(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("90.00"), 10);
        cadastrarProdutosDeExemplo(produto, produto1);

        List<Produto> encontrados = relatorio.listarProdutosComEstoqueBaixo(5);
        assertEquals(1, encontrados.size());
        assertSame(produto, encontrados.get(0));

    }

    @Test
    void deveRejeitarLimiteNegativo(){
        assertThrows(IllegalArgumentException.class, () -> relatorio.listarProdutosComEstoqueBaixo(-1));
    }

    @Test
    void deveOrdenarProdutosPorPrecoCrescente(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("90.00"), 10);
        cadastrarProdutosDeExemplo(produto, produto1);

        assertIterableEquals(
                List.of(produto1, produto),
                relatorio.listarProdutosPorPrecoCrescente()
        );
    }

    @Test
    void deveOrdenarProdutosPorPrecoDecrescente(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("90.00"), 10);
        cadastrarProdutosDeExemplo(produto, produto1);

        assertIterableEquals(
                List.of(produto, produto1),
                relatorio.listarProdutosPorPrecoDecrescente()
        );
    }

    @Test
    void deveCalcularValorTotalEstoque(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("90.00"), 10);
        cadastrarProdutosDeExemplo(produto, produto1);
        assertEquals(new BigDecimal("1219.80"), relatorio.calcularValorTotalEstoque());
    }

    @Test
    void deveRetornarZeroQuandoEstoqueEstiverVazio() {
        assertEquals(BigDecimal.ZERO, relatorio.calcularValorTotalEstoque());
    }
}
