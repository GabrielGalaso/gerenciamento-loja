package com.gabriel.gerenciamentoloja.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {
    public static Connection abrirConexao() throws SQLException {
        String url = System.getenv("LOJA_DB_URL");
        String usuario = System.getenv("LOJA_DB_USER");
        String senha = System.getenv("LOJA_DB_PASSWORD");

        return DriverManager.getConnection(url, usuario, senha);
    }
}
