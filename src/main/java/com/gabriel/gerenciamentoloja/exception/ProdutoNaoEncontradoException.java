package com.gabriel.gerenciamentoloja.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String message) {
        super(message);
    }
}
