package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;


public class RelatorioProdutoService {

    private final Repository<Produto, Integer> repositoryProduto;

    public RelatorioProdutoService(Repository<Produto, Integer> repositoryProduto) {
        if (repositoryProduto == null){
            throw new IllegalArgumentException("Insira um repositório");
        }
        this.repositoryProduto = repositoryProduto;
    }

    public List<Produto> listarProdutosComEstoqueBaixo(int limite){
        if (limite < 0){
            throw new IllegalArgumentException("Limite não pode estar negativo");
        }
        return repositoryProduto.listarTodos().stream()
                .sorted(Comparator.comparing(Produto::getNome))
                .filter(qnt -> qnt.getQuantidade() < limite)
                .toList();
    }

    public List<Produto> listarProdutosPorPrecoCrescente(){
        return repositoryProduto.listarTodos().stream()
                .sorted(Comparator.comparing(Produto::getPreco))
                .toList();
    }

    public List<Produto> listarProdutosPorPrecoDecrescente(){
        return repositoryProduto.listarTodos().stream()
                .sorted(Comparator.comparing(Produto::getPreco).reversed())
                .toList();
    }

    public BigDecimal calcularValorTotalEstoque(){
        return repositoryProduto.listarTodos().stream()
                .map(produto -> produto.getPreco()
                        .multiply(new BigDecimal(produto.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

