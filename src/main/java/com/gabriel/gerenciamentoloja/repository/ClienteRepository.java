package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.model.Cliente;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteRepository {
    private final Map<Integer, Cliente> clientes = new HashMap<>();

    public void cadastrar(Cliente cliente){
        if (cliente == null || clientes.containsKey(cliente.getId())){
            throw new IllegalArgumentException("Cliente já cadastrado ou cliente null");
        }
        clientes.put(cliente.getId(), cliente);
    }

    public Cliente buscarPorId(int id){
        if (!clientes.containsKey(id)){
            throw new IllegalArgumentException("Cliente não existe");
        }
        return clientes.get(id);
    }

    public Collection<Cliente> listarTodos(){
        final List<Cliente> values;
        return List.copyOf(clientes.values());

    }

    public void removerPorId(int id){
        if (!clientes.containsKey(id)){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }
        clientes.remove(id);
    }
}
