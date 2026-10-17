import com.gabriel.gerenciamentoloja.model.Cliente;
import com.gabriel.gerenciamentoloja.model.ItemPedido;
import com.gabriel.gerenciamentoloja.model.Pedido;
import com.gabriel.gerenciamentoloja.model.Produto;
import com.gabriel.gerenciamentoloja.repository.ProdutoRepository;
import com.gabriel.gerenciamentoloja.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoServiceTest {

    private ProdutoRepository repository;
    private PedidoService service;
    private Cliente cliente;


    @BeforeEach
    void configurar() {
        repository = new ProdutoRepository();
        service = new PedidoService(repository);
        cliente = new Cliente("Roberto@gmail.com", 1, "Roberto");

    }

    @Test
     void deveRejeitarPedidoNulo(){
        assertThrows(IllegalArgumentException.class, () -> service.verificarPedido(null));
    }

    @Test
    void deveRejeitarPedidoVazio(){
        Pedido pedido = new Pedido(1, cliente);
        assertThrows(IllegalArgumentException.class, () -> service.verificarPedido(pedido));
    }

    @Test
    void deveAceitarPedidoComItens(){
        Pedido pedido = new Pedido(1, cliente);
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        ItemPedido item = new ItemPedido(produto, 2);
        pedido.adicionarItem(item);
        assertDoesNotThrow(() -> service.verificarPedido(pedido));
    }

    @Test
    void deveAceitarPedidoComEstoqueSuficiente() {
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 3);
        pedido.adicionarItem(item);
        assertDoesNotThrow(() -> service.verificarEstoque(pedido));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRejeitarPedidoComEstoqueInsuficiente(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 5);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 8);
        pedido.adicionarItem(item);
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.verificarEstoque(pedido));
        assertEquals("Estoque insuficiente", excecao.getMessage());
    }

    @Test
    void deveAceitarPedidoComEstoqueExato(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 10);
        pedido.adicionarItem(item);
        assertDoesNotThrow(() -> service.verificarEstoque(pedido));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveRejeitarPedidoComProdutosRepetidos(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 5);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 3);
        ItemPedido item1 = new ItemPedido(produto, 3);
        pedido.adicionarItem(item);
        pedido.adicionarItem(item1);
        assertThrows(IllegalArgumentException.class, () -> service.verificarEstoque(pedido));

    }

    @Test
    void deveRejeitarProdutoNaoCadastrado(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 5);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 3);
        pedido.adicionarItem(item);
        assertThrows(IllegalArgumentException.class, () -> service.verificarEstoque(pedido));
    }

    @Test
    void deveReduzirEstoqueAoFinalizarPedido(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 3);
        pedido.adicionarItem(item);
        assertDoesNotThrow(() -> service.finalizarPedido(pedido));
        assertEquals(7, produto.getQuantidade());
    }

    @Test
    void deveImpedirFinalizacaoDuplicada(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 3);
        pedido.adicionarItem(item);
        assertDoesNotThrow(() -> service.finalizarPedido(pedido));
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.finalizarPedido(pedido));
        assertEquals(7, produto.getQuantidade());
        assertEquals("Pedido já finalizado", excecao.getMessage());
    }

    @Test
    void deveManterEstoqueQuandoInsuficiente(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        repository.cadastrar(produto);
        Pedido pedido = new Pedido(1, cliente);
        ItemPedido item = new ItemPedido(produto, 11);
        pedido.adicionarItem(item);
        assertThrows(IllegalArgumentException.class, () -> service.finalizarPedido(pedido));
        assertEquals(10, produto.getQuantidade());
    }

    @Test
    void deveAtualizarEstoqueDeDoisProdutos(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("159.90"), 10);

        repository.cadastrar(produto);
        repository.cadastrar(produto1);

        Pedido pedido = new Pedido(1, cliente);

        ItemPedido item = new ItemPedido(produto, 3);
        ItemPedido item1 = new ItemPedido(produto1, 3);

        pedido.adicionarItem(item);
        pedido.adicionarItem(item1);

        assertDoesNotThrow(() -> service.finalizarPedido(pedido));
        assertEquals(7, produto.getQuantidade());
        assertEquals(7, produto1.getQuantidade());

    }

    @Test
    void deveDescontarProdutosRepetidos(){
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);

        repository.cadastrar(produto);

        Pedido pedido = new Pedido(1, cliente);

        ItemPedido item = new ItemPedido(produto, 3);
        ItemPedido item1 = new ItemPedido(produto, 3);

        pedido.adicionarItem(item);
        pedido.adicionarItem(item1);

        assertDoesNotThrow(() -> service.finalizarPedido(pedido));
        assertEquals(4, produto.getQuantidade());

    }

    @Test
    void deveManterTodosOsEstoquesQuandoUmProdutoForInsuficiente() {
        Produto produto = new Produto(1, "Teclado", new BigDecimal("159.90"), 10);
        Produto produto1 = new Produto(2, "Mouse", new BigDecimal("159.90"), 5);

        repository.cadastrar(produto);
        repository.cadastrar(produto1);

        Pedido pedido = new Pedido(1, cliente);

        ItemPedido item = new ItemPedido(produto, 3);
        ItemPedido item1 = new ItemPedido(produto1, 7);

        pedido.adicionarItem(item);
        pedido.adicionarItem(item1);

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> service.finalizarPedido(pedido));
        assertEquals(5, produto1.getQuantidade());
        assertEquals(10, produto.getQuantidade());
        assertEquals("Estoque insuficiente", excecao.getMessage());

    }

}
