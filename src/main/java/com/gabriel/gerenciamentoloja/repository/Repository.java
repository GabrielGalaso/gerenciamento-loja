package com.gabriel.gerenciamentoloja.repository;


import java.util.Collection;

public interface Repository <T, ID>{

    T buscarPorId(ID id);

    void removerPorId(ID id);

    void cadastrar(T p);

    Collection<T> listarTodos();


}
