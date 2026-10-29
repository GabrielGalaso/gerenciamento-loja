package com.gabriel.gerenciamentoloja.service;

import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.repository.Repository;

public class PedidoServiceJdbc {

    private final Repository<Pedido, Integer> repositoryPedido;

    public PedidoServiceJdbc(Repository<Pedido, Integer> repositoryPedido) {
        if (repositoryPedido == null){
            throw new IllegalArgumentException("Insira um repositório");
        }
        this.repositoryPedido = repositoryPedido;
    }

    public void finalizarPedido(Pedido pedido) {
        if (pedido == null || pedido.getCliente() == null || pedido.getItens().isEmpty()){
            throw new IllegalArgumentException("Pedido inválido");
        }
        repositoryPedido.cadastrar(pedido);
    }
}
