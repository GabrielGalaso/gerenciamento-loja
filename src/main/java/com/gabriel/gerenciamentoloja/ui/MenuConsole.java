package com.gabriel.gerenciamentoloja.ui;

import com.gabriel.gerenciamentoloja.exception.*;
import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.Repository;
import com.gabriel.gerenciamentoloja.service.ExportacaoProdutoService;
import com.gabriel.gerenciamentoloja.service.PedidoServiceJdbc;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

public class MenuConsole {

    private final Repository<Produto, Integer> produtoRepository;
    private final Repository<Cliente, Integer> clienteRepository;
    private final Repository<Pedido, Integer>  pedidoRepository;
    private final PedidoServiceJdbc pedidoServiceJdbc;
    private final ExportacaoProdutoService exportacaoService;

    public MenuConsole(Repository<Produto, Integer> produtoRepository,
                       Repository<Cliente, Integer> clienteRepository,
                       Repository<Pedido, Integer>  pedidoRepository,
                       PedidoServiceJdbc pedidoServiceJdbc,
                       ExportacaoProdutoService exportacaoService) {

        if (produtoRepository == null || clienteRepository == null || pedidoRepository == null || pedidoServiceJdbc == null || exportacaoService == null) {
            throw new IllegalArgumentException("Repositório não pode ser nulo");
        }
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoServiceJdbc = pedidoServiceJdbc;
        this.exportacaoService = exportacaoService;

    }

    private final Scanner scanner = new Scanner(System.in);

    public void iniciar() {
        int op;
        while (true){
            exibirMenuPrincipal();
            op = lerInteiro("Escolha uma opção:");
            if (op == 0) break;
            switch (op){
                case 1 -> menuProdutos();
                case 2 -> menuClientes();
                case 3 -> menuPedidos();
                case 4 -> menuRelatorios();
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void exibirMenuPrincipal() {
        System.out.println("=========================================");
        System.out.println("SISTEMA DE GERENCIAMENTO DE LOJA");
        System.out.println("=========================================");
        System.out.println("[1]  Gerenciar produtos");
        System.out.println("[2]  Gerenciar clientes");
        System.out.println("[3]  Gerenciar pedidos");
        System.out.println("[4]  Relatorios e exportacao");
        System.out.println("                        ");
        System.out.println("[0]  Sair do sistema");
        System.out.println("-----------------------------------------");
    }

    private void exibirMenuProduto() {
        System.out.println("======= GERENCIAR PRODUTOS =======");
        System.out.println("[1]  Cadastrar produto");
        System.out.println("[2]  Listar produtos");
        System.out.println("[3]  Buscar produto por ID");
        System.out.println("[4]  Remover produto");
        System.out.println("                        ");
        System.out.println("[0] Voltar ao menu principal");
        System.out.println("    ");
    }

    private void exibirMenuCliente() {
        System.out.println("======= GERENCIAR CLIENTES =======");
        System.out.println("[1]  Cadastrar cliente");
        System.out.println("[2]  Listar clientes");
        System.out.println("[3]  Buscar cliente por ID");
        System.out.println("[4]  Remover cliente");
        System.out.println("                        ");
        System.out.println("[0] Voltar ao menu principal");
        System.out.println("    ");
    }

    private void exibirMenuPedido() {
        System.out.println("======= GERENCIAR PEDIDOS =======");
        System.out.println("[1]  Criar e finalizar pedido");
        System.out.println("[2]  Listar pedidos");
        System.out.println("[3]  Buscar pedido por ID");
        System.out.println("                        ");
        System.out.println("[0] Voltar ao menu principal");
        System.out.println("    ");
    }

    private void exibirMenuRelatorios() {
        System.out.println("======= RELATÓRIOS =======");
        System.out.println("[1]  Consultar faturamento total");
        System.out.println("[2] Exportar produtos para CSV");
        System.out.println("[0] Voltar ao menu principal");
        System.out.println("    ");
    }


    private int lerInteiro(String mensagem) {
        while (true){
            try {
                System.out.println(mensagem);
                int numero = Integer.parseInt(scanner.nextLine().trim());
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número inteiro.");
            }
        }
    }
    private BigDecimal lerDecimal(String mensagem) {
        while (true){
            try {
                System.out.println(mensagem);
                BigDecimal numero = new BigDecimal(scanner.nextLine().trim().replace(",", "."));
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite um número válido.");
            }
        }
    }
    private String lerSimNao(String mensagem) {
        while (true){
            System.out.println(mensagem);
            String resposta = scanner.nextLine().trim().toUpperCase();
                if (resposta.equals("S") || resposta.equals("N")) {
                    return resposta;
                }
            System.out.println("Entrada inválida! Digite apenas S ou N.");
        }
    }

    private int lerInteiroPositivo(String mensagem) {
        while (true){
            int positivo = lerInteiro(mensagem);
            if (positivo > 0){
                return positivo;
            }
            System.out.println("Valor inválido! Digite um número maior que zero.");
        }
    }

    private void menuProdutos(){
        while (true){
            exibirMenuProduto();
            int op = lerInteiro("Escolha uma opção:");
            switch (op){
                case 1 -> cadastrarProduto();
                case 2 -> listarProdutos();
                case 3 -> buscarProduto();
                case 4 -> removerProduto();
                case 0 ->{return;}
                default -> System.out.println("Opção inválida, tente novamente.");
                }
            }
        }
    private void cadastrarProduto() {
        try {
            System.out.println("Digite o nome do produto para cadastrar:");
            String nome = scanner.nextLine();
            int id = lerInteiroPositivo("Insira um ID para o produto:");
            BigDecimal preco = lerDecimal("Insira o preço do produto:");
            int quantidade = lerInteiro("Quantidade em estoque:");

            Produto produto = new Produto(id, nome, preco, quantidade);
            produtoRepository.cadastrar(produto);
            System.out.println("Produto cadastrado com sucesso!");

        } catch (IllegalArgumentException e){
            System.out.println("Dados inválidos: " + e.getMessage());
        } catch (ProdutoJaCadastradoException e ){
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private void listarProdutos(){
        System.out.println("Produtos listados");
        produtoRepository.listarTodos().forEach(System.out::println);
    }

    private void buscarProduto(){
        int id = lerInteiroPositivo("Insira o ID do produto que deseja buscar:");
        try {
            Produto encontrado = produtoRepository.buscarPorId(id);
            System.out.println(encontrado);

        } catch (ProdutoNaoEncontradoException e) {
            System.out.println("Produto não encontrado: " + e.getMessage());
        }
    }

    private void removerProduto(){
        int id = lerInteiroPositivo("Insira o ID do produto que deseja remover:");
        try {
            produtoRepository.removerPorId(id);
            System.out.println("Produto removido com sucesso!");
        }catch (ProdutoNaoEncontradoException e){
            System.out.println("Produto não encontrado: " + e.getMessage());
        }
        catch (ProdutoComPedidosException e ){
            System.out.println("Não foi possível remover: " + e.getMessage());
        }
    }

    private void menuClientes(){
        while (true){
            exibirMenuCliente();
            int op = lerInteiro("Escolha uma opção:");
            switch (op){
                case 1 -> cadastrarCliente();
                case 2 -> listarClientes();
                case 3 -> buscarCliente();
                case 4 -> removerCliente();
                case 0 ->{return;}
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void cadastrarCliente(){
        try {
            System.out.println("Digite o nome do cliente para cadastrar: ");
            String nome = scanner.nextLine();
            System.out.println("Digite o email do cliente: ");
            String email = scanner.nextLine();
            int id = lerInteiroPositivo("Insira um ID para o cliente:");

            Cliente cliente = new Cliente(email, id, nome);
            clienteRepository.cadastrar(cliente);
            System.out.println("Cliente cadastrado com sucesso!");
        }
        catch (IllegalArgumentException e){
            System.out.println("Dados inválidos: " + e.getMessage());
        }
        catch (ClienteJaCadastradoException e ){
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
        }
    }

    private void listarClientes(){
        Collection<Cliente> clientes = clienteRepository.listarTodos();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado!");
            return;
        }

        System.out.println("Clientes listados");
        clientes.forEach(System.out::println);
    }

    private void buscarCliente(){
        int id = lerInteiroPositivo("Insira o ID do cliente que deseja buscar:");
        try {
            Cliente encontrado = clienteRepository.buscarPorId(id);
            System.out.println(encontrado);

        } catch (ClienteNaoEncontradoException e) {
            System.out.println("Cliente não encontrado: " + e.getMessage());
        }
    }

    private void removerCliente(){
        int id = lerInteiroPositivo("Insira o ID do cliente que deseja remover:");
        try {
            clienteRepository.removerPorId(id);
            System.out.println("Cliente removido com sucesso!");
        }catch (ClienteNaoEncontradoException e){
            System.out.println("Cliente não encontrado: " + e.getMessage());
        }
        catch (ClienteComPedidosException e ){
            System.out.println("Não foi possível remover: " + e.getMessage());
        }
    }

    private void menuPedidos(){
        while (true){
            exibirMenuPedido();
            int op = lerInteiro("Escolha uma opção:");
            switch (op){
                case 1 -> criarEFinalizarPedido();
                case 2 -> listarPedidos();
                case 3 -> buscarPedido();
                case 0 ->{return;}
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void criarEFinalizarPedido(){
        int idPedido = lerInteiroPositivo("Insira o id do pedido");
        int idCliente = lerInteiroPositivo("Insira o id do cliente");
        Cliente encontrado;

        try {
            encontrado = clienteRepository.buscarPorId(idCliente);
        }catch (ClienteNaoEncontradoException e){
            System.out.println("Cliente não encontrado!");
            return;
        }

        Pedido pedido = new Pedido(idPedido, encontrado);

        while (true){
            System.out.println("Adicionando item: ");
            int idProduto = lerInteiroPositivo("Insira o ID do produto:");
            Produto produto;

           try {
               produto = produtoRepository.buscarPorId(idProduto);
           }catch (ProdutoNaoEncontradoException e){
               System.out.println("Produto inexistente! Tente novamente.");
               continue;
           }

            int quantidade = lerInteiroPositivo("Insira a quantidade:");

            ItemPedido item = new ItemPedido(produto, quantidade);
            pedido.adicionarItem(item);

            System.out.println("Item adicionado ao pedido!");

            String resposta = lerSimNao("Deseja adicionar outro item? (S/N)");

            if (resposta.equals("N")) {break;}
        }
        try {
            pedidoServiceJdbc.finalizarPedido(pedido);
            System.out.println("Pedido finalizado com sucesso!");
            System.out.println("Total: R$ " + pedido.calcularTotal());

        }catch (PedidoJaFinalizadoException e){
            System.out.println("Esse pedido já foi finalizado!");

        }catch (EstoqueInsuficienteException e) {
            System.out.println("Não foi possível finalizar: " + e.getMessage());
        }
    }

    private void listarPedidos(){
        System.out.println("Pedidos listados");
        pedidoRepository.listarTodos().forEach(System.out::println);
    }

    private void buscarPedido(){
        int id = lerInteiroPositivo("Insira o ID do pedido que deseja buscar:");
        try {
            Pedido encontrado = pedidoRepository.buscarPorId(id);
            System.out.println(encontrado);

        } catch (PedidoNaoEncontradoException e) {
            System.out.println("Pedido não encontrado: " + e.getMessage());
        }
    }

    private void menuRelatorios(){
        while (true){
            exibirMenuRelatorios();
            int op = lerInteiro("Escolha uma opção:");
            switch (op){
                case 1 -> exibirFaturamentoTotal();
                case 2 -> {
                    try {
                    exportacaoService.exportarProdutosCsv(Path.of("exportacoes", "produtos.csv"));

                    System.out.println("Produtos exportados com sucesso!");

                } catch (IOException e) {
                    System.out.println("Erro ao exportar produtos: " + e.getMessage());
                }
                }
                case 0 ->{return;}
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    private void exibirFaturamentoTotal() {
        Collection<Pedido> pedidos = pedidoRepository.listarTodos();
        BigDecimal total = BigDecimal.ZERO;

        for (Pedido pedido: pedidos){
            total = total.add(pedido.calcularTotal());
        }
        System.out.println("Faturamento total: R$ " + total);
    }

    }

