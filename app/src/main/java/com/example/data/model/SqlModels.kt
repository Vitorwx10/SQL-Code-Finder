package com.example.data.model

enum class SqlDatabase(val displayName: String, val shortName: String, val badgeColorHex: Long) {
    ALL("Todos os Bancos", "ANSI / Geral", 0xFF10B981),
    MYSQL("MySQL", "MySQL", 0xFF0284C7),
    POSTGRESQL("PostgreSQL", "PostgreSQL", 0xFF336791),
    SQL_SERVER("SQL Server", "T-SQL", 0xFFDC2626),
    ORACLE("Oracle DB", "Oracle", 0xFFD97706),
    SQLITE("SQLite", "SQLite", 0xFF0D9488)
}

enum class SqlCategory(val displayName: String, val description: String) {
    OVERVIEW("Visão Geral", "Panorama geral da biblioteca e atalhos rápidos"),
    DATA_TYPES("Tipos de Dados", "Definições de tipos para colunas e armazenamento"),
    COMMANDS("Comandos SQL", "Comandos DDL, DML, DCL e manipulação de tabelas"),
    FUNCTIONS("Funções SQL", "Funções nativas de agregação, texto, data e matemática"),
    JOINS("Joins e Relacionamentos", "Junções de tabelas, integridade referencial e chaves"),
    PRACTICAL("Exemplos Práticos", "Casos reais de negócio: estoques, vendas e clientes"),
    COMPARATOR("Comparador SQL", "Compare a sintaxe do mesmo recurso entre bancos"),
    FAVORITES("Favoritos", "Códigos e consultas que você marcou com estrela")
}

enum class SubCategory(val displayName: String) {
    DATA_TYPE_NUMERIC("Numéricos"),
    DATA_TYPE_TEXT("Texto e Caracteres"),
    DATA_TYPE_DATE("Data e Hora"),
    DATA_TYPE_BOOLEAN("Booleanos e Binários"),
    QUERY_BASIC("Consultas Básicas"),
    QUERY_FILTER("Filtros e Condições"),
    QUERY_AGGREGATION("Agrupamento e Agregação"),
    MANIPULATION_INSERT("Inserção de Dados"),
    MANIPULATION_UPDATE("Atualização de Dados"),
    MANIPULATION_DELETE("Exclusão de Dados"),
    DDL_TABLE("Criação e Estrutura"),
    DDL_ALTER("Modificação de Estrutura"),
    INTEGRITY_KEYS("Chaves e Restrições"),
    FUNCTION_STRING("Funções de Texto"),
    FUNCTION_DATE("Funções de Data/Hora"),
    FUNCTION_NUMERIC("Funções Matemáticas"),
    FUNCTION_LOGICAL("Funções Lógicas / Nulos"),
    JOIN_BASIC("Junções de Tabelas"),
    TRANSACTIONS("Transações e ACID")
}

enum class DifficultyLevel(val displayName: String, val level: Int) {
    BASIC("Básico", 1),
    INTERMEDIATE("Intermediário", 2),
    ADVANCED("Avançado", 3)
}

enum class Purpose(val displayName: String) {
    STORE_TEXT("Armazenar texto"),
    STORE_NUMBERS("Armazenar números"),
    STORE_DATES("Armazenar datas"),
    QUERY_RECORDS("Consultar registros"),
    FILTER_RESULTS("Filtrar resultados"),
    CALCULATE_VALUES("Calcular valores"),
    GROUP_INFO("Agrupar informações"),
    RELATE_TABLES("Relacionar tabelas"),
    MODIFY_RECORDS("Modificar registros"),
    DATA_INTEGRITY("Integridade e estrutura")
}

data class SqlCodeItem(
    val id: String,
    val name: String,
    val category: SqlCategory,
    val subcategory: SubCategory,
    val databases: List<SqlDatabase>,
    val difficulty: DifficultyLevel,
    val purpose: Purpose,
    val description: String,
    val syntax: String,
    val example: String,
    val explanation: String,
    val keywords: List<String>,
    val compatibilityNotes: String,
    val isPopular: Boolean = false
)

data class SqlComparison(
    val id: String,
    val title: String,
    val concept: String,
    val category: String,
    val sourceDb: SqlDatabase,
    val targetDb: SqlDatabase,
    val sourceSyntax: String,
    val targetSyntax: String,
    val similarities: String,
    val differences: String,
    val limitations: String,
    val migrationTips: String
)

data class PracticalScenario(
    val id: String,
    val title: String,
    val businessCase: String,
    val ddlSchema: String,
    val query: String,
    val objective: String,
    val explanation: String,
    val tags: List<String>,
    val compatibleDatabases: List<SqlDatabase>
)
