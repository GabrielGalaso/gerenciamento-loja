USE gerenciamento_loja;
CREATE TABLE produtos(
id INT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
preco DECIMAL(10,2) NOT NULL CHECK (preco > 0) ,
quantidade INT NOT NULL CHECK (quantidade >= 0) );

CREATE TABLE clientes(
id INT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE );

CREATE TABLE pedidos(
id INT PRIMARY KEY,
cliente_id INT NOT NULL,
FOREIGN KEY (cliente_id) REFERENCES clientes(id));

CREATE TABLE itens_pedido(
id INT PRIMARY KEY,
pedido_id INT NOT NULL ,
produto_id INT NOT NULL,
quantidade INT NOT NULL CHECK (quantidade > 0), 
preco_unitario DECIMAL(10,2) NOT NULL CHECK (preco_unitario > 0),
FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
FOREIGN KEY (produto_id) REFERENCES produtos(id));

ALTER TABLE itens_pedido
MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;

INSERT INTO itens_pedido(pedido_id, produto_id, quantidade, preco_unitario)
VALUES (1, 1, 1, 159.90);

INSERT INTO pedidos (id, cliente_id)
VALUES (1, 1);

INSERT INTO clientes(id, nome, email)
VALUES (1, 'Pedro', 'pedro@gmail.com');

INSERT INTO produtos (id, nome, preco, quantidade)
VALUES (1, 'Teclado', 159.90, 10);

SELECT * FROM itens_pedido;

SELECT * FROM pedidos;

SELECT * FROM clientes;

SELECT * FROM produtos;