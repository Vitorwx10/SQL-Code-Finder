package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeFunction
import com.example.ui.theme.CodeKeyword
import com.example.ui.theme.CodeNumber
import com.example.ui.theme.CodeOperator
import com.example.ui.theme.CodeString
import com.example.ui.theme.CodeType
import com.example.ui.theme.Slate100

object SqlSyntaxHighlighter {

    private val KEYWORDS = setOf(
        "SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "IN", "AS",
        "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "CROSS", "ON",
        "GROUP", "BY", "ORDER", "HAVING", "LIMIT", "OFFSET", "DISTINCT",
        "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE",
        "CREATE", "TABLE", "ALTER", "DROP", "TRUNCATE", "INDEX", "ADD", "COLUMN",
        "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "UNIQUE", "NOT", "NULL", "CHECK", "DEFAULT",
        "CASCADE", "RESTRICT", "TOP", "FETCH", "FIRST", "NEXT", "ROWS", "ONLY",
        "UNION", "ALL", "EXISTS", "BETWEEN", "LIKE", "ILIKE", "IS",
        "CASE", "WHEN", "THEN", "ELSE", "END", "STRICT", "AUTO_INCREMENT", "IDENTITY", "SERIAL"
    )

    private val FUNCTIONS = setOf(
        "COUNT", "SUM", "AVG", "MIN", "MAX",
        "CONCAT", "CONCAT_WS", "SUBSTRING", "SUBSTR", "LENGTH", "LEN",
        "UPPER", "LOWER", "TRIM", "LTRIM", "RTRIM", "REPLACE",
        "COALESCE", "NULLIF", "NOW", "CURRENT_TIMESTAMP", "GETDATE", "SYSDATE", "SYSDATETIME",
        "DATEDIFF", "DATEADD", "EXTRACT", "ROUND", "CAST", "CONVERT", "AGE"
    )

    private val DATA_TYPES = setOf(
        "INT", "INTEGER", "BIGINT", "SMALLINT", "TINYINT",
        "DECIMAL", "NUMERIC", "FLOAT", "REAL", "DOUBLE", "PRECISION",
        "CHAR", "VARCHAR", "VARCHAR2", "NVARCHAR", "TEXT", "CLOB", "BLOB", "BYTEA", "VARBINARY",
        "DATE", "TIME", "DATETIME", "TIMESTAMP", "TIMESTAMPTZ",
        "BOOLEAN", "BOOL", "BIT", "JSON", "JSONB", "UUID", "UNIQUEIDENTIFIER"
    )

    fun highlight(sql: String): AnnotatedString {
        return buildAnnotatedString {
            append(sql)

            // 1. Comments: -- to end of line
            val commentRegex = Regex("--.*$|/\\*.*?\\*/", RegexOption.MULTILINE)
            for (match in commentRegex.findAll(sql)) {
                addStyle(
                    SpanStyle(color = CodeComment, fontStyle = FontStyle.Italic),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 2. Strings: 'single quoted' or "double quoted"
            val stringRegex = Regex("'(?:''|[^'])*'|\"(?:\"\"|[^\"])*\"")
            for (match in stringRegex.findAll(sql)) {
                addStyle(
                    SpanStyle(color = CodeString),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 3. Numbers: integers and floats
            val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
            for (match in numberRegex.findAll(sql)) {
                addStyle(
                    SpanStyle(color = CodeNumber),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 4. Words (Keywords, Functions, Types)
            val wordRegex = Regex("\\b[A-Za-z_][A-Za-z0-9_]*\\b")
            for (match in wordRegex.findAll(sql)) {
                val word = match.value.uppercase()
                when {
                    KEYWORDS.contains(word) -> {
                        addStyle(
                            SpanStyle(color = CodeKeyword, fontWeight = FontWeight.Bold),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                    FUNCTIONS.contains(word) -> {
                        addStyle(
                            SpanStyle(color = CodeFunction, fontWeight = FontWeight.SemiBold),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                    DATA_TYPES.contains(word) -> {
                        addStyle(
                            SpanStyle(color = CodeType, fontWeight = FontWeight.SemiBold),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                }
            }

            // 5. Operators: = <> != <= >= || + - * /
            val operatorRegex = Regex("(\\|\\||<=|>=|!=|<>|[=<>+\\-*/])")
            for (match in operatorRegex.findAll(sql)) {
                addStyle(
                    SpanStyle(color = CodeOperator),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
    }
}
