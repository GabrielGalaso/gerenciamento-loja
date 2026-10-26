package com.gabriel.gerenciamentoloja.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class ItemPedido {
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoUnitario;

    public ItemPedido(Produto produto, int quantidade) {
        validarProduto(produto);
        validarQuantidade(quantidade);
        this.produto = produto;
        this.precoUnitario = produto.getPreco();
        this.quantidade = quantidade;
    }
    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario) {
        validarProduto(produto);
        validarQuantidade(quantidade);
        if (precoUnitario == null || precoUnitario.signum() <= 0) {
            throw new IllegalArgumentException("Preço unitário inválido");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal calcularSubtotal(){
        return BigDecimal.valueOf(quantidade).multiply(getPrecoUnitario());
    }


    private void validarProduto(Produto produto){
        if (produto == null){
            throw new IllegalArgumentException("Produto não pode ser null");
        }
    }

    private void validarQuantidade(int quantidade){
        if (quantidade <= 0){
            throw new IllegalArgumentException("Quantidade tem que ser acima de 0");
        }
    }

}
