package com.gabriel.gerenciamentoloja.repository.jdbc;

import com.gabriel.gerenciamentoloja.config.ConexaoFactory;
import com.gabriel.gerenciamentoloja.exception.PedidoJaFinalizadoException;
import com.gabriel.gerenciamentoloja.exception.PedidoNaoEncontradoException;
import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
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

public class PedidoRepositoryJdbc implements Repository<Pedido, Integer> {

    @Override
    public Pedido buscarPorId(Integer id) {
        String sql = """
        SELECT
            p.id AS pedido_id,
            c.id AS cliente_id,
            c.nome AS cliente_nome,
            c.email AS cliente_email
        FROM pedidos p
        INNER JOIN clientes c ON p.cliente_id = c.id
        WHERE p.id = ?;
        """;
        String sql1 = """
        SELECT
            ip.produto_id,
            ip.quantidade AS quantidade_comprada,
            ip.preco_unitario,
            p.nome,
            p.preco AS preco_atual,
            p.quantidade AS estoque_atual
        FROM itens_pedido ip
        INNER JOIN produtos p ON ip.produto_id = p.id
        WHERE ip.pedido_id = ?
        ORDER BY ip.id;
        """;

        if (id == null || id <= 0){
            throw new IllegalArgumentException("Id não encontrado");
        }

        try(Connection conexao = ConexaoFactory.abrirConexao();) {

            Pedido pedido;

            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()){
                        throw new PedidoNaoEncontradoException("Pedido não encontrado");
                    }
                    int idPedido = rs.getInt("pedido_id");

                    String email = rs.getString("cliente_email");
                    int idCliente = rs.getInt("cliente_id");
                    String nome = rs.getString("cliente_nome");

                    Cliente cliente = new Cliente(email,idCliente,nome);
                    pedido = new Pedido(idPedido, cliente);
                }
            }

            try (PreparedStatement psItens = conexao.prepareStatement(sql1)) {
                psItens.setInt(1, id);

                try (ResultSet rsItens = psItens.executeQuery()){
                    while (rsItens.next()) {
                        int idProduto = rsItens.getInt("produto_id");
                        String nomeProduto = rsItens.getString("nome");
                        BigDecimal precoAtual = rsItens.getBigDecimal("preco_atual");
                        int estoqueAtual  = rsItens.getInt("estoque_atual");

                        BigDecimal precoHistorico = rsItens.getBigDecimal("preco_unitario");
                        int quantidadeComprada = rsItens.getInt("quantidade_comprada");

                        Produto produto = new Produto(idProduto, nomeProduto, precoAtual, estoqueAtual);
                        ItemPedido itemPedido = new ItemPedido(produto, quantidadeComprada, precoHistorico);
                        pedido.adicionarItem(itemPedido);
                    }
                }
            }
            return pedido;

        }catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar pedido no banco", e);
        }
    }

    @Override
    public void removerPorId(Integer id) {
        String sqlItens = "DELETE FROM itens_pedido WHERE pedido_id = ?;";
        String sqlPedidos = "DELETE FROM pedidos WHERE id = ?;";

        if (id == null || id <= 0){
            throw new IllegalArgumentException("Id não encontrado para remover");
        }
        try(Connection conexao = ConexaoFactory.abrirConexao()){
            try {
                conexao.setAutoCommit(false);

                try (PreparedStatement ps = conexao.prepareStatement(sqlItens);) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conexao.prepareStatement(sqlPedidos);) {

                    ps.setInt(1, id);

                    int linhasAfetadas = ps.executeUpdate();

                    if (linhasAfetadas != 1) {
                        throw new PedidoNaoEncontradoException(  "Pedido não encontrado para remoção: " + id);
                    }
                }
                conexao.commit();

            }catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }
                throw e;
            }
        }catch (SQLException e) {
            throw new IllegalStateException("Erro ao deletar pedido no banco", e);
        }
    }

    @Override
    public void cadastrar(Pedido pedido) {
        String sqlPedido = """
        INSERT INTO pedidos (id, cliente_id)
        VALUES (?, ?);
        """;
        String sqlItens = """
        INSERT INTO itens_pedido
        (pedido_id, produto_id, quantidade, preco_unitario)
        VALUES (?, ?, ?, ?);
        """;
        String sqlEstoque = """
        UPDATE produtos
        SET quantidade = quantidade - ?
        WHERE id = ? AND quantidade >= ?;
        """;

        if (pedido == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido não pode ser nulo ou vazio");
        }

        try (Connection conexao = ConexaoFactory.abrirConexao()) {
            try {
                conexao.setAutoCommit(false);

            try(PreparedStatement ps = conexao.prepareStatement(sqlPedido)) {
                ps.setInt(1, pedido.getId());
                ps.setInt(2, pedido.getCliente().getId());

                int linhasAfetadas;

                try {
                    linhasAfetadas = ps.executeUpdate();

                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        throw new PedidoJaFinalizadoException("Pedido já finalizado: " + pedido.getId());
                    }
                    throw e;
                }
                if (linhasAfetadas != 1) {
                    throw new IllegalStateException("O pedido não foi cadastrado");
                }

            }

            try (PreparedStatement ps = conexao.prepareStatement(sqlItens)) {
                for (ItemPedido item : pedido.getItens()) {
                    ps.setInt(1, pedido.getId());
                    ps.setInt(2, item.getProduto().getId());
                    ps.setInt(3, item.getQuantidade());
                    ps.setBigDecimal(4, item.getPrecoUnitario());

                    int linhasAfetadas = ps.executeUpdate();

                    if (linhasAfetadas != 1) {
                        throw new IllegalStateException("O item do pedido não foi cadastrad");
                    }
                }

            }

            try (PreparedStatement ps = conexao.prepareStatement(sqlEstoque)) {
                for (ItemPedido item : pedido.getItens()) {
                    ps.setInt(1, item.getQuantidade());
                    ps.setInt(2, item.getProduto().getId());
                    ps.setInt(3, item.getQuantidade());

                    int linhasAfetadas = ps.executeUpdate();
                    if (linhasAfetadas != 1) {
                        throw new IllegalStateException("O produto não existe ou estoque insuficiente");
                    }
                }

            }

            conexao.commit();

        }catch (SQLException | RuntimeException e) {
            try {
                conexao.rollback();
            } catch (SQLException erroRollback) {
                e.addSuppressed(erroRollback);
            }
            throw e;
        }

    } catch (SQLException e) {
        throw new IllegalStateException("Erro na transação do pedido", e);
        }
    }


    @Override
    public Collection<Pedido> listarTodos() {
        String sql = """
         SELECT id
         FROM pedidos
         ORDER BY id;
         """;
        List<Pedido> pedidos = new ArrayList<>();

        try(Connection conexao = ConexaoFactory.abrirConexao();
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();) {

            while (rs.next()){
                int id = rs.getInt("id");
                Pedido pedido = buscarPorId(id);
                pedidos.add(pedido);
            }
        } catch (SQLException e ){
            throw new IllegalStateException("erro ao listar pedidos", e);
        }
        return List.copyOf(pedidos);
    }
}
