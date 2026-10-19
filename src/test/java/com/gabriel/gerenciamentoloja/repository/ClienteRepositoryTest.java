package com.gabriel.gerenciamentoloja.repository;

import com.gabriel.gerenciamentoloja.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ClienteRepositoryTest {

    private ClienteRepository clienteRepository;
    private Cliente cliente;

    @BeforeEach
    void configurar(){
        cliente = new Cliente("pedrosanto@gmail.com", 1, "Pedro");
        clienteRepository = new ClienteRepository();
    }

    @Test
    void deveCadastrarCliente(){
        clienteRepository.cadastrar(cliente);
        Cliente encontrado = clienteRepository.buscarPorId(cliente.getId());
        assertSame(cliente, encontrado);
    }

    @Test
    void deveRejeitarClienteNulo() {
        assertThrows(IllegalArgumentException.class, () -> clienteRepository.cadastrar(null));
    }

    @Test
    void deveRejeitarIdDuplicado(){
        Cliente cliente1 = new Cliente("caua@gmail.com",1 ,"Cauã");
        clienteRepository.cadastrar(cliente);
        assertThrows(IllegalArgumentException .class, () -> clienteRepository.cadastrar(cliente1));

        Cliente encontrado = clienteRepository.buscarPorId(cliente.getId());
        assertSame(cliente, encontrado);
    }

    @Test
    void deveBuscarClientePorId(){
        clienteRepository.cadastrar(cliente);
        Cliente encontrado = clienteRepository.buscarPorId(cliente.getId());
        assertSame(encontrado, cliente);
    }

    @Test
    void deveRejeitarBuscaDeIdInexistente(){
        clienteRepository.cadastrar(cliente);
        IllegalArgumentException excecao =  assertThrows(IllegalArgumentException .class, () -> clienteRepository.buscarPorId(2));
        assertEquals("Cliente não existe",excecao.getMessage());
    }

    @Test
    void deveListarTodosClientes(){
        Cliente cliente1 = new Cliente("caua@gmail.com",2 ,"Cauã");
        Cliente cliente2 = new Cliente("davi@gmail.com",3 ,"Davi");
        clienteRepository.cadastrar(cliente);
        clienteRepository.cadastrar(cliente1);
        clienteRepository.cadastrar(cliente2);
        assertTrue(clienteRepository.listarTodos().contains(cliente));
        assertTrue(clienteRepository.listarTodos().contains(cliente1));
        assertTrue(clienteRepository.listarTodos().contains(cliente2));
        assertEquals(3, clienteRepository.listarTodos().size());
    }

    @Test
    void deveRemoverClientePorId(){
        clienteRepository.cadastrar(cliente);
        clienteRepository.removerPorId(cliente.getId());
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> clienteRepository.buscarPorId(cliente.getId()));
        assertEquals("Cliente não existe",excecao.getMessage());
    }

    @Test
    void deveRejeitarRemocaoInexistente(){
        IllegalArgumentException excecao =  assertThrows(IllegalArgumentException .class, () -> clienteRepository.removerPorId(999));
        assertEquals("Id não encontrado para remover",excecao.getMessage());
    }

    @Test
    void deveProtegerColecaoRetornada(){
        clienteRepository.cadastrar(cliente);
        assertThrows(UnsupportedOperationException.class, () -> clienteRepository.listarTodos().clear());
        assertSame(cliente, clienteRepository.buscarPorId(cliente.getId()));
    }

}
