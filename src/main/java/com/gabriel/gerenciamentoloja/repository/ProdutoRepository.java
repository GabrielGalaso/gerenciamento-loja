package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.exception.ProdutoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Produto;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProdutoRepository implements Repository<Produto, Integer> {

    private final Map<Integer, Produto> produtos = new HashMap<>();

    @Override
    public void cadastrar(Produto produto){
        if (produto == null || produtos.containsKey(produto.getId())){
            throw new IllegalArgumentException("Produto já cadastrado ou produto null");
        }
       produtos.put(produto.getId(), produto);
    }

    @Override
    public Produto buscarPorId(Integer id){
        if (!produtos.containsKey(id)){
            throw new ProdutoNaoEncontradoException("Produto não encontrado");
        }
        return produtos.get(id);
    }

    @Override
    public Collection<Produto> listarTodos(){
        final List<Produto> values;
        values = List.copyOf(produtos.values());
        return values;
    }

    @Override
    public void removerPorId(Integer id){
        if (!produtos.containsKey(id)){
            throw new ProdutoNaoEncontradoException("Id não encontrado para remover");
        }
        produtos.remove(id);
    }


}
