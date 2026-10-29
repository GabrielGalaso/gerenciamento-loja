package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.ClienteComPedidosException;
import com.gabriel.gerenciamentoloja.exception.ProdutoComPedidosException;
import com.gabriel.gerenciamentoloja.exception.ProdutoJaCadastradoException;
import com.gabriel.gerenciamentoloja.exception.ProdutoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.Repository;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ProdutoRepositoryJdbc implements Repository<Produto, Integer> {

    @Override
    public Produto buscarPorId(Integer integer) {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, integer);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()){
                    int id = rs.getInt("id");
                    String nome =rs.getString("nome");
                    BigDecimal preco =  rs.getBigDecimal("preco");
                    int quantidade = rs.getInt("quantidade");

                    Produto produto = new Produto(id, nome, preco, quantidade);
                    return produto;
                }
            }
        }catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar produto no banco", e);
        }
        throw new ProdutoNaoEncontradoException("Produto não encontrado");
    }

    @Override
    public void removerPorId(Integer id) {
        String sql = "DELETE FROM produtos WHERE id = ?;";

        if (id == null || id <= 0){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new ProdutoNaoEncontradoException("Produto ID " + id + " não encontrado");
            }

        }catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new ProdutoComPedidosException("Produto ID " + id + " possui pedidos vinculados!");
            }
            throw new IllegalStateException("Erro ao deletar produto no banco", e);
        }
    }

    @Override
    public void cadastrar(Produto produto) {
        String sql = """
        INSERT INTO produtos (id, nome, preco, quantidade)
        VALUES (?, ?, ?, ?);
        """;
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);) {

            ps.setInt(1, produto.getId());
            ps.setString(2, produto.getNome());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setInt(4,produto.getQuantidade());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas != 1) {
                throw new IllegalStateException("O produto não foi cadastrado");
            }

        }catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ProdutoJaCadastradoException("Produto com ID " + produto.getId() + " já cadastrado!");
            }
            throw new IllegalStateException("Erro ao cadastrar produto no banco", e);
        }
    }

    @Override
    public Collection<Produto> listarTodos() {
        String sql = """
         SELECT id, nome, preco, quantidade
         FROM produtos;
         """;
        List<Produto> produtos = new ArrayList<>();

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();) {

            while (rs.next()){
                int id = rs.getInt("id");
                String nome =rs.getString("nome");
                BigDecimal preco =  rs.getBigDecimal("preco");
                int quantidade = rs.getInt("quantidade");

                Produto produto = new Produto(id, nome, preco, quantidade);
                produtos.add(produto);
            }
        } catch (SQLException e ){
            throw new IllegalStateException("erro ao listar produtos", e);
        }

        return List.copyOf(produtos);
    }
}
