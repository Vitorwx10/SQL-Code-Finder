package com.example.data.repository

import com.example.data.model.SqlComparison
import com.example.data.model.SqlDatabase

object SqlComparisonsData {
    val comparisons = listOf(
        SqlComparison(
            id = "comp-autoincrement",
            title = "Chave Autoincremento / Sequência",
            concept = "Chave Primária com Incremento Automático",
            category = "Criação de Tabelas",
            sourceDb = SqlDatabase.MYSQL,
            targetDb = SqlDatabase.POSTGRESQL,
            sourceSyntax = "CREATE TABLE clientes (\n    id INT AUTO_INCREMENT PRIMARY KEY,\n    nome VARCHAR(100)\n);",
            targetSyntax = "CREATE TABLE clientes (\n    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n    -- ou histórico: id SERIAL PRIMARY KEY\n    nome VARCHAR(100)\n);",
            similarities = "Ambos geram números inteiros sequenciais automáticos únicos ao inserir linhas sem especificar o ID.",
            differences = "MySQL usa a cláusula 'AUTO_INCREMENT'. PostgreSQL usa 'GENERATED ALWAYS AS IDENTITY' (padrão SQL:2003) ou o pseudotipo 'SERIAL' que cria uma SEQUENCE vinculada.",
            limitations = "No PostgreSQL IDENTITY, tentar inserir um ID explicitamente sem OVERRIDING SYSTEM VALUE gera erro. No MySQL AUTO_INCREMENT aceita inserção manual sem restrição extra.",
            migrationTips = "Ao migrar do MySQL para o Postgres, prefira GENERATED ALWAYS AS IDENTITY em vez de SERIAL. Lembre-se de sincronizar a sequência com setval() se importar dados com IDs existentes."
        ),
        SqlComparison(
            id = "comp-pagination",
            title = "Limitação e Paginação de Resultados",
            concept = "LIMIT / OFFSET vs TOP vs FETCH FIRST",
            category = "Consultas",
            sourceDb = SqlDatabase.MYSQL,
            targetDb = SqlDatabase.SQL_SERVER,
            sourceSyntax = "SELECT *\nFROM produtos\nORDER BY preco DESC\nLIMIT 10 OFFSET 20;",
            targetSyntax = "SELECT *\nFROM produtos\nORDER BY preco DESC\nOFFSET 20 ROWS\nFETCH NEXT 10 ROWS ONLY;\n-- Ou apenas primeiros sem paginação:\nSELECT TOP (10) * FROM produtos ORDER BY preco DESC;",
            similarities = "Ambos permitem restringir o número de registros processados e navegar por páginas de dados ordenados.",
            differences = "MySQL usa 'LIMIT n OFFSET m' no final da consulta. SQL Server antigo usava 'TOP (n)' no SELECT; a partir do SQL Server 2012 adotou o padrão ANSI 'OFFSET m ROWS FETCH NEXT n ROWS ONLY'.",
            limitations = "No SQL Server, a cláusula OFFSET...FETCH exige obrigatoriamente a presença de ORDER BY, enquanto no MySQL o LIMIT funciona mesmo sem ORDER BY (embora sem ordem garantida).",
            migrationTips = "Substitua chamadas de LIMIT no MySQL pela cláusula ANSI 'OFFSET x ROWS FETCH NEXT y ROWS ONLY' no SQL Server. Nunca use TOP para paginação com salto (offset)."
        ),
        SqlComparison(
            id = "comp-string-concat",
            title = "Concatenação de Strings",
            concept = "Junção de textos e tratamento de nulos",
            category = "Funções de Texto",
            sourceDb = SqlDatabase.MYSQL,
            targetDb = SqlDatabase.POSTGRESQL,
            sourceSyntax = "SELECT CONCAT(primeiro_nome, ' ', sobrenome) AS nome,\n       CONCAT_WS(' ', rua, numero, bairro) AS endereco\nFROM pessoas;",
            targetSyntax = "SELECT primeiro_nome || ' ' || sobrenome AS nome,\n       CONCAT_WS(' ', rua, numero, bairro) AS endereco\nFROM pessoas;",
            similarities = "Ambos fornecem funções para unir cadeias de texto e suportam a função CONCAT_WS para ignorar nulos com separador.",
            differences = "No PostgreSQL, o operador nativo padrão ANSI é '||'. No MySQL, por padrão '||' atua como operador lógico OR (a menos que o modo PIPES_AS_CONCAT esteja habilitado).",
            limitations = "No PostgreSQL, concatenar com '||' contendo um valor NULL resulta em NULL (ex: 'Olá' || NULL vira NULL). No MySQL CONCAT(str, NULL) também retorna NULL.",
            migrationTips = "Utilize CONCAT_WS() para máxima portabilidade caso queira ignorar valores NULL entre os componentes do texto sem quebrar o resultado final."
        ),
        SqlComparison(
            id = "comp-string-types",
            title = "Texto de Tamanho Variável e Grande",
            concept = "VARCHAR vs VARCHAR2 vs NVARCHAR vs TEXT",
            category = "Tipos de Dados",
            sourceDb = SqlDatabase.SQL_SERVER,
            targetDb = SqlDatabase.ORACLE,
            sourceSyntax = "CREATE TABLE artigos (\n    titulo NVARCHAR(200),\n    conteudo VARCHAR(MAX)\n);",
            targetSyntax = "CREATE TABLE artigos (\n    titulo VARCHAR2(200 CHAR),\n    conteudo CLOB\n);",
            similarities = "Ambos armazenam strings de comprimento dinâmico e suportam caracteres internacionais Unicode.",
            differences = "SQL Server usa NVARCHAR para Unicode UTF-16 e VARCHAR(MAX) para grandes textos. Oracle usa VARCHAR2 (com especificador CHAR para contagem em caracteres) e CLOB para grandes blocos.",
            limitations = "VARCHAR2 no Oracle possui limite tradicional de 4000 bytes (ou 32767 se MAX_STRING_SIZE=EXTENDED). Strings maiores exigem o tipo CLOB, que requer funções especiais como DBMS_LOB.",
            migrationTips = "No Oracle, sempre declare VARCHAR2(n CHAR) para evitar truncamento com acentos e emojis em bancos UTF-8."
        ),
        SqlComparison(
            id = "comp-dates",
            title = "Data e Hora Atual",
            concept = "CURRENT_TIMESTAMP, NOW(), GETDATE(), SYSDATE",
            category = "Funções de Data/Hora",
            sourceDb = SqlDatabase.SQL_SERVER,
            targetDb = SqlDatabase.MYSQL,
            sourceSyntax = "SELECT GETDATE() AS agora,\n       SYSDATETIMEOFFSET() AS agora_com_tz;",
            targetSyntax = "SELECT NOW() AS agora,\n       CURRENT_TIMESTAMP AS agora_padrao;",
            similarities = "Ambos capturam a data e hora do relógio da máquina onde o servidor de banco está rodando.",
            differences = "SQL Server usa primordialmente GETDATE() e SYSDATETIME(). MySQL aceita NOW() e a palavra-chave ANSI CURRENT_TIMESTAMP.",
            limitations = "GETDATE() não inclui fuso horário (Time Zone offset). No MySQL, TIMESTAMP é convertido automaticamente para UTC no armazenamento, enquanto DATETIME salva o valor bruto.",
            migrationTips = "Troque chamadas de GETDATE() por NOW() ou pelo padrão ANSI CURRENT_TIMESTAMP ao migrar para MySQL ou PostgreSQL."
        ),
        SqlComparison(
            id = "comp-booleans",
            title = "Tipos Booleanos e Flags",
            concept = "BOOLEAN vs BIT vs NUMBER(1)",
            category = "Tipos de Dados",
            sourceDb = SqlDatabase.POSTGRESQL,
            targetDb = SqlDatabase.SQL_SERVER,
            sourceSyntax = "CREATE TABLE usuarios (\n    ativo BOOLEAN DEFAULT TRUE\n);\nSELECT * FROM usuarios WHERE ativo IS TRUE;",
            targetSyntax = "CREATE TABLE usuarios (\n    ativo BIT DEFAULT 1\n);\nSELECT * FROM usuarios WHERE ativo = 1;",
            similarities = "Ambos permitem armazenar estados binários (verdadeiro / falso / nulo).",
            differences = "PostgreSQL possui tipo BOOLEAN nativo completo com literais TRUE/FALSE. SQL Server usa o tipo numérico BIT (1 para true, 0 para false).",
            limitations = "No SQL Server não existe 'WHERE ativo' diretamente na sintaxe antiga; é necessário fazer 'WHERE ativo = 1'.",
            migrationTips = "Ao migrar do Postgres para o SQL Server, substitua TRUE por 1 e FALSE por 0 nas inserções e filtros."
        ),
        SqlComparison(
            id = "comp-upsert",
            title = "Inserir ou Atualizar (Upsert / Merge)",
            concept = "ON DUPLICATE KEY UPDATE vs ON CONFLICT vs MERGE",
            category = "Manipulação de Dados",
            sourceDb = SqlDatabase.MYSQL,
            targetDb = SqlDatabase.POSTGRESQL,
            sourceSyntax = "INSERT INTO estoque (produto_id, quantidade)\nVALUES (101, 5)\nON DUPLICATE KEY UPDATE quantidade = quantidade + 5;",
            targetSyntax = "INSERT INTO estoque (produto_id, quantidade)\nVALUES (101, 5)\nON CONFLICT (produto_id)\nDO UPDATE SET quantidade = estoque.quantidade + 5;",
            similarities = "Ambos executam uma inserção se a chave não existir ou uma atualização atômica se colidir com uma chave única.",
            differences = "MySQL usa 'ON DUPLICATE KEY UPDATE'. PostgreSQL usa 'ON CONFLICT (coluna_alvo) DO UPDATE SET'. SQL Server e Oracle usam a instrução 'MERGE INTO'.",
            limitations = "No PostgreSQL, a cláusula ON CONFLICT exige explicitar a coluna ou restrição que possui o índice exclusivo (UNIQUE ou PRIMARY KEY).",
            migrationTips = "A sintaxe ON CONFLICT do PostgreSQL é mais precisa e segura que o MySQL, pois permite múltiplos tratamentos dependendo da coluna conflitante."
        ),
        SqlComparison(
            id = "comp-coalesce",
            title = "Tratamento de Valores Nulos",
            concept = "COALESCE vs IFNULL vs ISNULL vs NVL",
            category = "Funções Lógicas",
            sourceDb = SqlDatabase.SQL_SERVER,
            targetDb = SqlDatabase.ORACLE,
            sourceSyntax = "SELECT ISNULL(telefone, 'Sem Telefone') AS tel,\n       COALESCE(celular, fixo, 'Nenhum') AS tel_prioridade\nFROM contatos;",
            targetSyntax = "SELECT NVL(telefone, 'Sem Telefone') AS tel,\n       COALESCE(celular, fixo, 'Nenhum') AS tel_prioridade\nFROM contatos;",
            similarities = "Ambos suportam a função universal padrão ANSI COALESCE(val1, val2, ...).",
            differences = "Funções proprietárias de 2 parâmetros: SQL Server usa ISNULL(); Oracle usa NVL(); MySQL usa IFNULL().",
            limitations = "Oracle considera strings vazias '' como equivalentes a NULL, enquanto SQL Server, MySQL e Postgres tratam '' como string de tamanho zero diferente de NULL.",
            migrationTips = "Sempre adote COALESCE() no seu código fonte, pois ela funciona de forma idêntica e portátil em TODOS os bancos de dados."
        ),
        SqlComparison(
            id = "comp-sqlite-types",
            title = "Tipagem Estrita vs Dinâmica no SQLite",
            concept = "Type Affinity vs Tipagem Forte Relacional",
            category = "Tipos de Dados",
            sourceDb = SqlDatabase.MYSQL,
            targetDb = SqlDatabase.SQLITE,
            sourceSyntax = "CREATE TABLE itens (\n    id INT PRIMARY KEY,\n    preco DECIMAL(10,2) NOT NULL\n);",
            targetSyntax = "CREATE TABLE itens (\n    id INTEGER PRIMARY KEY AUTOINCREMENT,\n    preco REAL NOT NULL\n) STRICT; -- Modo STRICT a partir do SQLite 3.37+",
            similarities = "Permitem criar tabelas e armazenar os mesmos dados da aplicação.",
            differences = "Tradicionalmente o SQLite usa afinidade dinâmica (você pode guardar texto numa coluna INT). Apenas tabelas criadas com 'STRICT' impõem tipos rígidos.",
            limitations = "SQLite não possui tipos nativos de data (armazena como TEXT ISO8601, REAL julian ou INTEGER epoch) e nem BOOLEAN (usa 0 e 1).",
            migrationTips = "Para apps Android com Room ou SQLite nativo, armazene datas como ISO8601 Strings ou Epoch Milliseconds (BIGINT) para consistência."
        )
    )
}
