package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.ClienteNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Cliente;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRepositoryJdbcTest {

    private ClienteRepositoryJdbc clienteRepositoryJdbc;
    private Cliente cliente;
    private boolean cadastradoNesteTeste;

    @BeforeEach
    void configurar() throws SQLException {
        cliente = new Cliente("pedro@gmail.com", 5, "Pedro");
        clienteRepositoryJdbc = new ClienteRepositoryJdbc();
        cadastradoNesteTeste = false;
        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());
        }
    }

    @AfterEach
    void limpar() throws SQLException {
        if (!cadastradoNesteTeste) {
            return;
        }
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conexao = ConexaoFactory.abrirConexao()) {

            assertEquals("gerenciamento_loja_teste", conexao.getCatalog());

            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setInt(1, cliente.getId());
                ps.executeUpdate();
            }
        }
    }

    @Test
    void deveCadastrarClienteNoBanco() {
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;

        Cliente encontrado = clienteRepositoryJdbc.buscarPorId(cliente.getId());

        assertEquals(cliente.getId(), encontrado.getId());
        assertEquals(cliente.getNome(), encontrado.getNome());
        assertEquals(cliente.getEmail(), encontrado.getEmail());
    }

    @Test
    void deveBuscarClientePorId(){
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;
        Cliente encontrado = assertDoesNotThrow(() -> clienteRepositoryJdbc.buscarPorId(cliente.getId()));

        assertEquals(cliente.getId(), encontrado.getId());
        assertEquals(cliente.getEmail(), encontrado.getEmail());
    }

    @Test
    void deveRejeitarBuscaDeClienteInexistente(){
        ClienteNaoEncontradoException excecao =  assertThrows(ClienteNaoEncontradoException.class, () -> clienteRepositoryJdbc.buscarPorId(999999));
        assertEquals("Cliente não encontrado",excecao.getMessage());
    }

    @Test
    void deveRejeitarCadastroDuplicado(){
        Cliente cliente1 = new Cliente("outro@exemplo.com", cliente.getId(), "Outro");
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;

        IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> clienteRepositoryJdbc.cadastrar(cliente1));

        SQLException causa = assertInstanceOf(SQLException.class, excecao.getCause());

        assertEquals(1062, causa.getErrorCode());
    }

    @Test
    void deveRejeitarEmailDuplicado(){
        Cliente cliente1 = new Cliente("pedro@gmail.com", 4, "Pedro");
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;

        IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> clienteRepositoryJdbc.cadastrar(cliente1));

        SQLException causa = assertInstanceOf(SQLException.class, excecao.getCause());

        assertEquals(1062, causa.getErrorCode());

    }

    @Test
    void deveRejeitarClienteNulo(){
        assertThrows(IllegalArgumentException.class, () -> clienteRepositoryJdbc.cadastrar(null));
    }

    @Test
    void deveListarClientesDoBanco(){
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;
        Collection<Cliente> encontrados = clienteRepositoryJdbc.listarTodos();
        assertTrue(encontrados.stream().anyMatch(p -> p.getId() == cliente.getId()));
    }

    @Test
    void deveRemoverClienteDoBanco(){
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;
        clienteRepositoryJdbc.removerPorId(cliente.getId());
        ClienteNaoEncontradoException excecao = assertThrows(ClienteNaoEncontradoException.class, () -> clienteRepositoryJdbc.buscarPorId(cliente.getId()));
        assertEquals("Cliente não encontrado",excecao.getMessage());
    }

    @Test
    void deveRejeitarRemocaoDeClienteInexistente(){
        ClienteNaoEncontradoException excecao = assertThrows(ClienteNaoEncontradoException.class, () -> clienteRepositoryJdbc.removerPorId(999999));
        assertEquals("O cliente não foi deletado", excecao.getMessage());
    }

    @Test
    void deveProtegerListaDeClientes(){
        clienteRepositoryJdbc.cadastrar(cliente);
        cadastradoNesteTeste = true;
        assertThrows(UnsupportedOperationException.class, () -> clienteRepositoryJdbc.listarTodos().clear());
        Cliente encontrado = clienteRepositoryJdbc.buscarPorId(cliente.getId());
        assertEquals(cliente.getId(), encontrado.getId());
        assertEquals(cliente.getNome(), encontrado.getNome());
        assertEquals(cliente.getEmail(), encontrado.getEmail());
    }
}
