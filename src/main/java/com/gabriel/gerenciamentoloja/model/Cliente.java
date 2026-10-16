package com.gabriel.gerenciamentoloja.model;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class Cliente {
    private final int id;
    private String nome;
    private String email;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_+%-]+(?:\\.[A-Za-z0-9_+%-]+)*@" +
                    "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+" +
                    "[A-Za-z]{2,}$"
    );

    public Cliente(String email, int id, String nome) {
        setEmail(email);
        setNome(nome);
        this.id = id;
        validarId();
    }

    private void validarId(){
        if (id <= 0){
            throw new IllegalArgumentException("O id deve ser maior que zero");
        }

    }

    public void setEmail(String email) {
        if (email == null){
            throw new IllegalArgumentException("Email não pode ser nulo");
        }

        String emailNormalizado = email.strip().toLowerCase();

        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new IllegalArgumentException("Email inválido");
        }

        this.email = emailNormalizado;

    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O nome não pode estar vazio");
        }
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "email='" + email + '\'' +
                ", id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
