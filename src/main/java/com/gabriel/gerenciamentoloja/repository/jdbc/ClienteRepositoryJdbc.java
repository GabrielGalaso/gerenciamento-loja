package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.ClienteComPedidosException;
import com.gabriel.gerenciamentoloja.exception.ClienteJaCadastradoException;
import com.gabriel.gerenciamentoloja.exception.ClienteNaoEncontradoException;
import com.gabriel.gerenciamentoloja.exception.ProdutoJaCadastradoException;
import com.gabriel.gerenciamentoloja.model.Cliente;

import com.gabriel.gerenciamentoloja.repository.Repository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClienteRepositoryJdbc implements Repository<Cliente, Integer> {

    @Override
    public Cliente buscarPorId(Integer id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    id = rs.getInt("id");
                    String nome =rs.getString("nome");
                    String email = rs.getString("email");

                    Cliente cliente = new Cliente(email, id, nome);
                    return cliente;
                }
            }
        }catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar cliente no banco", e);
        }
        throw new ClienteNaoEncontradoException("Cliente não encontrado");
    }


    @Override
    public void removerPorId(Integer id) {
        String sql = "DELETE FROM clientes WHERE id = ?;";

        if (id == null || id <= 0){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new ClienteNaoEncontradoException("O cliente não foi deletado");
            }

        }catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new ClienteComPedidosException("Não foi possíve remover o cliente: " + id);
            }
            throw new IllegalStateException("Erro ao deletar cliente no banco", e);
        }
    }

    @Override
    public void cadastrar(Cliente cliente) {
        String sql = """
        INSERT INTO clientes (id, nome, email)
        VALUES (?, ?, ?);
        """;
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNome());
            ps.setString(3, cliente.getEmail());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas != 1) {
                throw new IllegalStateException("O cliente não foi cadastrado");
            }

        }catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ClienteJaCadastradoException("Cliente com ID " + cliente.getId() + " já cadastrado!");
            }
            throw new IllegalStateException("Erro ao cadastrar cliente no banco", e);
        }
    }

    @Override
    public Collection<Cliente> listarTodos() {
        String sql = """
         SELECT *
         FROM clientes;
         """;
        List<Cliente> clientes = new ArrayList<>();

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();) {

            while (rs.next()){
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String email = rs.getString("email");

                Cliente cliente = new Cliente(email, id, nome);
                clientes.add(cliente);
            }
        } catch (SQLException e ){
            throw new IllegalStateException("erro ao listar clientes", e);
        }

        return List.copyOf(clientes);
    }
}
