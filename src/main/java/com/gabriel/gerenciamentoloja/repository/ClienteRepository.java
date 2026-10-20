package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.model.Cliente;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteRepository implements Repository<Cliente, Integer> {
    private final Map<Integer, Cliente> clientes = new HashMap<>();

    @Override
    public void cadastrar(Cliente cliente){
        if (cliente == null || clientes.containsKey(cliente.getId())){
            throw new IllegalArgumentException("Cliente já cadastrado ou cliente null");
        }
        clientes.put(cliente.getId(), cliente);
    }

    @Override
    public Cliente buscarPorId(Integer id){
        if (!clientes.containsKey(id)){
            throw new IllegalArgumentException("Cliente não existe");
        }
        return clientes.get(id);
    }


    @Override
    public Collection<Cliente> listarTodos(){
        return List.copyOf(clientes.values());

    }

    @Override
    public void removerPorId(Integer id){
        if (!clientes.containsKey(id)){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }
        clientes.remove(id);
    }
}
