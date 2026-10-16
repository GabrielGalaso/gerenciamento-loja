package com.gabriel.gerenciamentoloja.model;


import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class Pedido {
    private final int id;
    private final Cliente cliente;
    private final List<ItemPedido> itens = new ArrayList<>();

    public Pedido(int id, Cliente cliente) {
        validarCliente(cliente);
        validarId(id);
        this.id = id;
        this.cliente = cliente;
    }

    private void validarCliente(Cliente cliente){
        if (cliente == null){
            throw new IllegalArgumentException("O cliente não pode ser null");
        }
    }

    private void validarId(int id){
        if (id <= 0){
            throw new IllegalArgumentException("O id deve ser maior que zero");
        }

    }

    public void adicionarItem(ItemPedido item){
        if (item == null){
            throw new IllegalArgumentException("Adicione algum pedido");
        }
        itens.add(item);
    }
    
    public BigDecimal calcularTotal(){
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido valor: itens){
          total = valor.calcularSubtotal().add(total);
        }
        return total;
    }

    public List<ItemPedido> getItens() {
        return List.copyOf(itens);
    }
}
