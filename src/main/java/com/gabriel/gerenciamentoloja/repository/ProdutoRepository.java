package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.model.Produto;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProdutoRepository {

    private final Map<Integer, Produto> produtos = new HashMap<>();

    public void cadastrar(Produto produto){
        if (produto == null || produtos.containsKey(produto.getId())){
            throw new IllegalArgumentException("Produto já cadastrado ou produto null");
        }
       produtos.put(produto.getId(), produto);
    }

    public Produto buscarPorId(int id){
        if (!produtos.containsKey(id)){
            throw new IllegalArgumentException("Produto não existe");
        }
        return produtos.get(id);
    }

    public Collection<Produto> listarTodos(){
        final List<Produto> values;
        values = List.copyOf(produtos.values());
        return values;
    }

    public void removerPorId(int id){
        if (!produtos.containsKey(id)){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }
        produtos.remove(id);
    }


}
