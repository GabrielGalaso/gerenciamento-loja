package com.gabriel.gerenciamentoloja.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void configurar() {
        cliente = new Cliente("pedro@gmail.com", 1, "Pedro");
    }

    @Test
    void deveCriarClienteValido(){
        assertEquals("pedro@gmail.com", cliente.getEmail());
        assertEquals(1, cliente.getId());
        assertEquals("Pedro", cliente.getNome());
    }

    @Test
    void deveRejeitarIdInvalido(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedro@gmail.com", -1, "Pedro"));
    }

    @Test
    void deveRejeitarNomeEmBranco(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedro@gmail.com", 1, "  "));
    }

    @Test
    void deveRejeitarNomeNull(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedro@gmail.com", 1, null));
    }
    @Test
    void deveRejeitarEmailInvalido(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedro@gmail..com", 1, "Pedro"));
    }

    @Test
    void deveNormalizarEmail(){
        Cliente cliente = new Cliente("PEDRO@GMAIL.COM  ", 1, "Pedro");
        assertEquals("pedro@gmail.com", cliente.getEmail());
    }

    @Test
    void deveAlterarNome(){
        assertDoesNotThrow(() -> cliente.setNome("Pedro Santos"));
        assertEquals("Pedro Santos", cliente.getNome());
    }

    @Test
    void deveRejeitarIdZero(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedro@gmail.com", 0, "Pedro"));
    }

    @Test
    void deveRejeitarEmailNulo(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente(null, 1, "Pedro"));
    }

    @Test
    void deveRejeitarEmailSemArroba(){
        assertThrows(IllegalArgumentException.class, () -> cliente = new Cliente("pedrogmail.com", 1, "Pedro"));
    }

    @Test
    void deveNormalizarEmailAoAlterar(){
        cliente.setEmail("  PEDRO@GMAIL.COM  ");
        assertEquals("pedro@gmail.com", cliente.getEmail());
    }

    @Test
    void devePreservarNomeAposAlteracaoInvalida(){
        assertThrows(IllegalArgumentException.class, () -> cliente.setNome("  "));
        assertEquals("Pedro", cliente.getNome());
    }

    @Test
    void devePreservarEmailAposAlteracaoInvalida(){
        assertThrows(IllegalArgumentException.class, () -> cliente.setEmail("Pedrogmail.com   "));
        assertEquals("pedro@gmail.com", cliente.getEmail());
    }

}
