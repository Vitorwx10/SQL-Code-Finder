package com.example.data.repository

import com.example.data.model.PracticalScenario
import com.example.data.model.SqlDatabase

object SqlScenariosData {
    val scenarios = listOf(
        PracticalScenario(
            id = "sc-customers",
            title = "1. Cadastro Completo de Clientes",
            businessCase = "Criação de cadastro seguro com validação de CPF único, e-mail obrigatório, telefone e data de adesão.",
            ddlSchema = """CREATE TABLE clientes (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);""",
            query = """-- Inserção de novo cliente e consulta de validação
INSERT INTO clientes (nome, cpf, email, telefone)
VALUES ('Ana Carolina Ramos', '123.456.789-00', 'ana.ramos@exemplo.com', '(11) 98765-4321');

-- Consulta de clientes ativos ordenados pelos mais recentes
SELECT id, nome, email, telefone, data_cadastro
FROM clientes
WHERE ativo = TRUE
ORDER BY data_cadastro DESC;""",
            objective = "Garantir integridade cadastral sem duplicação de dados fiscais (CPF) e facilitar contato.",
            explanation = "Usa UNIQUE para impedir contas duplicadas e GENERATED ALWAYS AS IDENTITY / AUTO_INCREMENT para chave primária segura.",
            tags = listOf("Clientes", "DDL", "Cadastro", "UNIQUE", "NOT NULL"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-products",
            title = "2. Cadastro de Produtos com Validações",
            businessCase = "Catálogo de mercadorias com regras de preço positivo e controle de estoque mínimo.",
            ddlSchema = """CREATE TABLE produtos (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku VARCHAR(30) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    estoque_atual INT DEFAULT 0,
    estoque_minimo INT DEFAULT 5,
    CONSTRAINT chk_preco_positivo CHECK (preco_venda > 0),
    CONSTRAINT chk_estoque_valido CHECK (estoque_atual >= 0 AND estoque_minimo >= 0)
);""",
            query = """INSERT INTO produtos (sku, nome, categoria, preco_venda, estoque_atual, estoque_minimo)
VALUES 
    ('NOTE-DELL-G15', 'Notebook Gamer G15 Core i7', 'Informática', 5499.00, 12, 3),
    ('MOU-LOG-MX3', 'Mouse Sem Fio MX Master 3', 'Periféricos', 499.90, 25, 5),
    ('TEC-MEC-RGB', 'Teclado Mecânico Compacto', 'Periféricos', 289.00, 4, 10);

SELECT sku, nome, categoria, preco_venda, estoque_atual
FROM produtos
ORDER BY categoria, nome;""",
            objective = "Evitar inconsistências de preços negativos ou estoques abaixo de zero através de restrições CHECK no próprio motor do banco.",
            explanation = "O motor SQL rejeita automaticamente qualquer transação que tente salvar valores negativos.",
            tags = listOf("Produtos", "Validações", "CHECK", "SKU", "Catálogo"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-inventory",
            title = "3. Controle e Histórico de Estoque",
            businessCase = "Rastreabilidade de movimentações de estoque (entradas de fornecedores, saídas por vendas e devoluções).",
            ddlSchema = """CREATE TABLE movimentacoes_estoque (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    produto_id INT NOT NULL,
    tipo_movimento VARCHAR(10) NOT NULL CHECK (tipo_movimento IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    quantidade INT NOT NULL CHECK (quantidade > 0),
    data_movimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(100),
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);""",
            query = """-- Cálculo do saldo real consolidado a partir do razão de movimentações
SELECT 
    p.id AS produto_id,
    p.nome AS produto,
    COALESCE(SUM(CASE WHEN m.tipo_movimento = 'ENTRADA' THEN m.quantidade ELSE 0 END), 0) AS total_entradas,
    COALESCE(SUM(CASE WHEN m.tipo_movimento = 'SAIDA' THEN m.quantidade ELSE 0 END), 0) AS total_saidas,
    (COALESCE(SUM(CASE WHEN m.tipo_movimento = 'ENTRADA' THEN m.quantidade ELSE 0 END), 0) -
     COALESCE(SUM(CASE WHEN m.tipo_movimento = 'SAIDA' THEN m.quantidade ELSE 0 END), 0)) AS saldo_calculado
FROM produtos p
LEFT JOIN movimentacoes_estoque m ON p.id = m.produto_id
GROUP BY p.id, p.nome
ORDER BY saldo_calculado ASC;""",
            objective = "Garantir auditoria total de estoque através de partidas dobradas / extrato de lançamentos imutáveis.",
            explanation = "Usa CASE WHEN condicional dentro da agregação SUM para totalizar entradas e saídas separadamente.",
            tags = listOf("Estoque", "Auditoria", "CASE WHEN", "SUM", "Saldo"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-sales-orders",
            title = "4. Registro de Vendas e Itens do Pedido",
            businessCase = "Modelagem relacional mestre-detalhe para emissão de pedidos e cálculo do total da venda.",
            ddlSchema = """CREATE TABLE pedidos (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id INT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMADO',
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE itens_pedido (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id)
);""",
            query = """-- Inserir pedido e seus itens
INSERT INTO pedidos (cliente_id) VALUES (1);

INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario)
VALUES 
    (1, 1, 1, 5499.00),
    (1, 2, 2, 499.90);

-- Resumo com total calculado por pedido
SELECT 
    p.id AS pedido_id,
    c.nome AS cliente,
    p.data_pedido,
    COUNT(i.id) AS qtd_itens_distintos,
    SUM(i.quantidade * i.preco_unitario) AS valor_total_pedido
FROM pedidos p
INNER JOIN clientes c ON p.cliente_id = c.id
INNER JOIN itens_pedido i ON p.id = i.pedido_id
GROUP BY p.id, c.nome, p.data_pedido;""",
            objective = "Congelar o preço_unitario praticado no momento da venda para que futuras alterações na tabela de produtos não alterem o histórico fiscal.",
            explanation = "Utiliza INNER JOIN duplo com agregação SUM multiplicando quantidade por preço unitário.",
            tags = listOf("Vendas", "Mestre-Detalhe", "INNER JOIN", "Faturamento", "Histórico"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-order-lookup",
            title = "5. Consulta Detalhada de Pedidos com Clientes",
            businessCase = "Extrato completo de pedidos para a tela de atendimento e rastreamento da expedição.",
            ddlSchema = "-- Utiliza as tabelas clientes, pedidos e itens_pedido já criadas.",
            query = """SELECT 
    p.id AS codigo_pedido,
    c.nome AS nome_cliente,
    c.email,
    p.data_pedido,
    p.status,
    prod.nome AS produto_comprado,
    item.quantidade,
    item.preco_unitario,
    (item.quantidade * item.preco_unitario) AS subtotal_item
FROM pedidos p
JOIN clientes c ON p.cliente_id = c.id
JOIN itens_pedido item ON p.id = item.pedido_id
JOIN produtos prod ON item.produto_id = prod.id
WHERE p.status = 'CONFIRMADO'
ORDER BY p.data_pedido DESC, p.id;""",
            objective = "Apresentar visão analítica linha a linha de tudo o que foi adquirido em cada ordem de compra.",
            explanation = "Combinação de 4 tabelas relacionais via JOIN, projetando colunas descritivas em vez de apenas identificadores numéricos.",
            tags = listOf("Consultas", "JOIN Múltiplo", "Expedição", "Relatório"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-out-of-stock",
            title = "6. Identificação de Produtos Sem Estoque (Zerados)",
            businessCase = "Alerta gerencial imediato de produtos com saldo igual a zero para remoção do ar na loja virtual.",
            ddlSchema = "-- Consulta executada sobre a tabela produtos.",
            query = """SELECT 
    id,
    sku,
    nome,
    categoria,
    preco_venda,
    estoque_atual
FROM produtos
WHERE estoque_atual <= 0
ORDER BY preco_venda DESC;""",
            objective = "Detectar rupturas de estoque na loja para evitar compras frustradas de clientes.",
            explanation = "Filtro rápido no WHERE `estoque_atual <= 0`. Deve ser indexado com índice parcial ou composto se executado com alta frequência.",
            tags = listOf("Estoque Zero", "Ruptura", "WHERE", "Alerta"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-low-stock-alert",
            title = "7. Alerta de Reposição (Abaixo do Estoque Mínimo)",
            businessCase = "Geração automática da lista de compras para itens cuja quantidade está perigosamente baixa.",
            ddlSchema = "-- Avalia a relação entre estoque_atual e estoque_minimo na tabela produtos.",
            query = """SELECT 
    sku,
    nome,
    categoria,
    estoque_atual,
    estoque_minimo,
    (estoque_minimo - estoque_atual) AS quantidade_a_comprar,
    (estoque_minimo * 2 - estoque_atual) AS sugestao_pedido_seguranca
FROM produtos
WHERE estoque_atual < estoque_minimo
ORDER BY (estoque_minimo - estoque_atual) DESC;""",
            objective = "Apoiar a equipe de suprimentos a emitir ordens de compra antes que os produtos esgotem por completo.",
            explanation = "Compara duas colunas da mesma linha no WHERE e realiza cálculo aritmético da diferença a ser reposta.",
            tags = listOf("Compras", "Estoque Mínimo", "Alerta", "Suprimentos"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-sales-by-period",
            title = "8. Total de Vendas por Período (Mês e Ano)",
            businessCase = "Relatório executivo financeiro com total faturado, volume de pedidos e ticket médio mensal.",
            ddlSchema = "-- Baseado na tabela pedidos e itens_pedido.",
            query = """-- Agrupamento mensal com faturamento e ticket médio
-- (Exemplo ANSI / PostgreSQL / MySQL)
SELECT 
    EXTRACT(YEAR FROM p.data_pedido) AS ano,
    EXTRACT(MONTH FROM p.data_pedido) AS mes,
    COUNT(DISTINCT p.id) AS total_pedidos,
    SUM(i.quantidade * i.preco_unitario) AS faturamento_total,
    ROUND(SUM(i.quantidade * i.preco_unitario) / COUNT(DISTINCT p.id), 2) AS ticket_medio
FROM pedidos p
JOIN itens_pedido i ON p.id = i.pedido_id
WHERE p.status = 'CONFIRMADO'
GROUP BY EXTRACT(YEAR FROM p.data_pedido), EXTRACT(MONTH FROM p.data_pedido)
ORDER BY ano DESC, mes DESC;""",
            objective = "Apresentar os principais KPIs de vendas consolidados por competência mensal.",
            explanation = "Usa EXTRACT para derivar ano/mês da data do pedido e COUNT(DISTINCT) para contar pedidos sem duplicar por item.",
            tags = listOf("Financeiro", "DRE", "Ticket Médio", "EXTRACT", "GROUP BY"),
            compatibleDatabases = listOf(SqlDatabase.POSTGRESQL, SqlDatabase.MYSQL, SqlDatabase.ORACLE)
        ),
        PracticalScenario(
            id = "sc-sales-by-customer",
            title = "9. Quantidade e Valor de Vendas por Cliente (Ranking)",
            businessCase = "Segmentação de clientes VIPs com maior volume de compras para campanhas de fidelidade.",
            ddlSchema = "-- Junção entre clientes, pedidos e itens_pedido.",
            query = """SELECT 
    c.id AS cliente_id,
    c.nome AS cliente,
    c.email,
    COUNT(DISTINCT p.id) AS total_compras,
    COALESCE(SUM(i.quantidade * i.preco_unitario), 0) AS valor_gasto_total,
    MAX(p.data_pedido) AS data_ultima_compra
FROM clientes c
LEFT JOIN pedidos p ON c.id = p.cliente_id AND p.status = 'CONFIRMADO'
LEFT JOIN itens_pedido i ON p.id = i.pedido_id
GROUP BY c.id, c.nome, c.email
HAVING COUNT(DISTINCT p.id) > 0
ORDER BY valor_gasto_total DESC;""",
            objective = "Descobrir quem são os clientes que geram 80% da receita da empresa (Análise de Pareto).",
            explanation = "LEFT JOIN com filtro de pedidos confirmados, agrupado pelos dados do cliente e ordenado pelo valor total decrescente.",
            tags = listOf("Clientes VIP", "Ranking", "Pareto", "LTV", "Agrupamento"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQL_SERVER, SqlDatabase.ORACLE, SqlDatabase.SQLITE)
        ),
        PracticalScenario(
            id = "sc-top-selling-products",
            title = "10. Top 10 Produtos Mais Vendidos da Loja",
            businessCase = "Descobrir quais mercadorias possuem o maior giro de vendas em unidades e receita gerada.",
            ddlSchema = "-- Junção entre produtos e itens_pedido.",
            query = """-- Top 10 mais vendidos (PostgreSQL / MySQL / SQLite)
SELECT 
    prod.id,
    prod.sku,
    prod.nome,
    prod.categoria,
    SUM(item.quantidade) AS total_unidades_vendidas,
    SUM(item.quantidade * item.preco_unitario) AS receita_gerada
FROM produtos prod
JOIN itens_pedido item ON prod.id = item.produto_id
JOIN pedidos ped ON item.pedido_id = ped.id
WHERE ped.status = 'CONFIRMADO'
GROUP BY prod.id, prod.sku, prod.nome, prod.categoria
ORDER BY total_unidades_vendidas DESC
LIMIT 10;""",
            objective = "Identificar os carros-chefes de venda para otimizar compras de fornecedor e promoções na página principal.",
            explanation = "Agrupa por produto, totaliza as unidades vendidas e ordena pelo topo, cortando o resultado nos 10 primeiros com LIMIT.",
            tags = listOf("Top 10", "Mais Vendidos", "LIMIT", "Faturamento", "Ranking"),
            compatibleDatabases = listOf(SqlDatabase.MYSQL, SqlDatabase.POSTGRESQL, SqlDatabase.SQLITE)
        )
    )
}
