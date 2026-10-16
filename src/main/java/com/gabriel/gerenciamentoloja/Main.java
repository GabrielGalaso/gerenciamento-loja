package com.gabriel.gerenciamentoloja;

import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.ClienteRepository;
import com.gabriel.gerenciamentoloja.repository.ProdutoRepository;
import com.gabriel.gerenciamentoloja.service.PedidoService;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) {

        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 2);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("80.00"), 10);
        Produto produto2 = new Produto(2, "Camera", new BigDecimal("80.00"), 5);

        Cliente cliente = new Cliente("Gabriel@gmail.com", 1, "Gabriel  ");
        Cliente cliente1 = new Cliente("Gabriel@gmail.com", 1, "Gabriel  ");
        ItemPedido item = new ItemPedido(produto, 2);
        ItemPedido item2 = new ItemPedido(produto1, 2);
        Pedido pedido = new Pedido(1, cliente);
        pedido.adicionarItem(item);
        pedido.adicionarItem(item2);
//        System.out.println(pedido);
//        System.out.println(pedido.calcularTotal());

        ProdutoRepository produtoRepository = new ProdutoRepository();
        produtoRepository.cadastrar(produto);
        produtoRepository.cadastrar(produto1);
//        System.out.println("Produtos: "+repository.listarTodos());

        ClienteRepository clienteRepository = new ClienteRepository();
        clienteRepository.cadastrar(cliente);
//        clienteRepository.cadastrar(cliente1);
//        System.out.println(clienteRepository.listarTodos());

        PedidoService pedidoService = new PedidoService(produtoRepository);
        pedidoService.finalizarPedido(pedido);
//        pedidoService.finalizarPedido(pedido);
//        System.out.println(produtoRepository.buscarPorId(2).getQuantidade());

    }


}
