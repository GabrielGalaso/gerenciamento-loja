package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportacaoProdutoService {

    private final Repository<Produto, Integer> repositoryProduto;

    public ExportacaoProdutoService(Repository<Produto, Integer> repositoryProduto) {
        if (repositoryProduto == null){
            throw new IllegalArgumentException("Insira um repositório");
        }
        this.repositoryProduto = repositoryProduto;
    }

    public void exportarProdutosCsv(Path destino) throws IOException {
        if (destino == null) {
            throw new IllegalArgumentException("Destino vazio");
        }
        Path pasta = destino.toAbsolutePath().getParent();
        Files.createDirectories(pasta);

        try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            writer.write("id;nome;preco;quantidade");
            writer.newLine();

            for (Produto produto : repositoryProduto.listarTodos()) {
                String linha = produto.getId()
                        + ";" + produto.getNome()
                        + ";" + produto.getPreco().toPlainString()
                        + ";" + produto.getQuantidade();
                writer.write(linha);
                writer.newLine();
            }
        }

    }
}
