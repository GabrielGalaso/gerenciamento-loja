package com.gabriel.gerenciamentoloja.model;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Produto {
    private final int id;
    private String nome;
    private BigDecimal preco;
    private int quantidade;

//    Constructor
    public Produto(int id, String nome, BigDecimal preco, int quantidade) {
        this.id = id;
        this.quantidade = quantidade;

        setNome(nome);
        setPreco(preco);
        estoqueNaoPodeSerNegativo();
    }

//    Métodos

    public void adicionarAoEstoque(int quantidade){
        if (quantidade > 0){
            this.quantidade += quantidade;
        }else {
            throw new IllegalArgumentException("Não podemos adicionar números negativos e nem 0");
        }

    }

    public void retirarDoEstoque(int quantidade){
        if (quantidade <= getQuantidade() && quantidade > 0){
            this.quantidade -= quantidade;
        }else {
            throw new IllegalArgumentException("Não podemos retirar mais do que o disponível ou inserir valores negativos");
        }

    }

    private void estoqueNaoPodeSerNegativo(){
        if (getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativo");
        }
    }


// Getters e Setters

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O nome não pode estar vazio");
        }
        this.nome = nome;
    }

    public void setPreco(BigDecimal preco) {

        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new IllegalArgumentException("O preço não pode ser negativo ou nulo");
        }
        this.preco = preco;
    }


    @Override
    public String toString() {

        return "Produto{" +
                "nome='" + getNome() + '\'' +
                ", preco=" + getPreco() +
                ", quantidade=" + getQuantidade() +
                ", id=" + getId() +
                '}';

    }
}
