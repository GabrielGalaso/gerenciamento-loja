package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.exception.EstoqueInsuficienteException;
import com.gabriel.gerenciamentoloja.exception.PedidoJaFinalizadoException;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PedidoService{

    private final Set<Integer> pedidosFinalizados = new HashSet<>();
    private final Repository<Produto, Integer> repositoryProduto;

    public PedidoService(Repository<Produto, Integer> repositoryProduto) {
        if (repositoryProduto == null){
            throw new IllegalArgumentException("Insira um repositório");
        }
        this.repositoryProduto = repositoryProduto;
    }

    public void verificarPedido(Pedido pedido) {
        if (pedido == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido não pode ser nulo ou vazio");
        }
    }

    private Map<Integer, Integer> agruparQuantidades(Pedido pedido) {
        Map<Integer, Integer> quantidadesPorProduto = new HashMap<>();

        for (ItemPedido item : pedido.getItens()) {
            quantidadesPorProduto.merge(item.getProduto().getId(), item.getQuantidade(), Integer::sum);
        }
        return quantidadesPorProduto;
    }

    public void verificarEstoque(Pedido pedido){
        verificarPedido(pedido);

        Map<Integer, Integer> quantidadesPorProduto = agruparQuantidades(pedido);

            for (Map.Entry<Integer, Integer> quant : quantidadesPorProduto.entrySet()){
                int idProduto  = quant.getKey();
                int quantidadeTotalAcumulado = quant.getValue();
                Produto produtoCadastrado = repositoryProduto.buscarPorId(idProduto);

                if (produtoCadastrado.getQuantidade() < quantidadeTotalAcumulado){
                    throw new EstoqueInsuficienteException("Estoque insuficiente");
                }
            }

    }

    public void finalizarPedido(Pedido pedido){
        verificarPedido(pedido);

        if (pedidosFinalizados.contains(pedido.getId())) {
            throw new PedidoJaFinalizadoException("Pedido já finalizado");
        }

        verificarEstoque(pedido);

        Map<Integer, Integer> quantidadesPorProduto = agruparQuantidades(pedido);

        for (Map.Entry<Integer, Integer> quant : quantidadesPorProduto.entrySet()){
            int idProduto  = quant.getKey();
            int quantidadeTotalAcumulado = quant.getValue();
            Produto produtoCadastrado = repositoryProduto.buscarPorId(idProduto);

            produtoCadastrado.retirarDoEstoque(quantidadeTotalAcumulado);
        }

        pedidosFinalizados.add(pedido.getId());

    }

    }


