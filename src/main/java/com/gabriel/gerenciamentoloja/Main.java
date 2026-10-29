package com.gabriel.gerenciamentoloja;

import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.ClienteRepository;
import com.gabriel.gerenciamentoloja.repository.Repository;
import com.gabriel.gerenciamentoloja.repository.jdbc.ClienteRepositoryJdbc;
import com.gabriel.gerenciamentoloja.repository.jdbc.PedidoRepositoryJdbc;
import com.gabriel.gerenciamentoloja.repository.jdbc.ProdutoRepositoryJdbc;
import com.gabriel.gerenciamentoloja.service.ExportacaoProdutoService;
import com.gabriel.gerenciamentoloja.service.PedidoServiceJdbc;
import com.gabriel.gerenciamentoloja.ui.MenuConsole;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Repository<Produto, Integer> produtoRepository = new ProdutoRepositoryJdbc();
        Repository<Cliente, Integer> clienteRepository = new ClienteRepositoryJdbc();
        Repository<Pedido, Integer> pedidoRepository = new PedidoRepositoryJdbc();
        PedidoServiceJdbc pedidoServiceJdbc = new PedidoServiceJdbc(pedidoRepository);
        ExportacaoProdutoService exportacaoService = new ExportacaoProdutoService(produtoRepository);

        MenuConsole menuConsole = new MenuConsole(produtoRepository, clienteRepository, pedidoRepository, pedidoServiceJdbc, exportacaoService);
        menuConsole.iniciar();

    }


}
