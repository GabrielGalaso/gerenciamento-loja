package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportacaoProdutoServiceTest {

    private ProdutoRepository repository;
    private ExportacaoProdutoService exportacao;

    @TempDir
    Path pastaTemporaria;

    @BeforeEach
    void configurar() {
        repository = new ProdutoRepository();
        exportacao = new ExportacaoProdutoService(repository);
    }
    private void cadastrarProdutosDeExemplo(Produto produto, Produto produto1) {
        repository.cadastrar(produto);
        repository.cadastrar(produto1);
    }

    @Test
    void deveExportarProdutosParaCsv() throws IOException{
        Path destino = pastaTemporaria.resolve("produtos.csv");
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("90.00"), 10);
        cadastrarProdutosDeExemplo(produto, produto1);
        exportacao.exportarProdutosCsv(destino);

        assertTrue(Files.exists(destino));
        List<String> linhas = Files.readAllLines(destino, StandardCharsets.UTF_8);
        assertEquals(3, linhas.size());
        assertEquals("id;nome;preco;quantidade", linhas.get(0));
        assertTrue(linhas.contains("1;Teclado;159.90;2"));
        assertTrue(linhas.contains("2;Mouse;90.00;10"));
    }
}
