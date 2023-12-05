package com.example.plugins.Schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Serializable
data class ExposedProduto(val nome: String, val preco: Float, val quant_estoque: Int, val categoria: String, val caracteristas: String, val avaliacao_geral: Float, val imagem_url: String)
@Serializable
data class ReadProduto(val id: Int, val nome: String, val preco: Float, val quant_estoque: Int, val categoria: String, val caracteristas: String, val avaliacao_geral: Float, val imagem_url: String)


class ProdutoSchema(private val database: Database) {
    object Produto : Table() {
        val id = integer("id").autoIncrement()
        val nome = varchar("nome", 100)
        val preco = float("preco").check {(it greaterEq 0)}
        val quant_estoque = integer("quant_estoque")
        val categoria = varchar("categoria", 100)
        val caracteristas = varchar("caracteristas", 255)
        val avaliacao_geral = float("avaliacao_geral").check {(it greaterEq 0) and (it lessEq 5)}
        val imagem_url = varchar("imagem_url", 255)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedProduto): Int = dbQuery {
       Produto.insert {
            it[nome] = insert.nome
            it[preco] = insert.preco
            it[quant_estoque] = insert.quant_estoque
            it[categoria] = insert.categoria
            it[caracteristas] = insert.caracteristas
            it[avaliacao_geral] = insert.avaliacao_geral
            it[imagem_url] = insert.imagem_url
        }[Produto.id]
    }

    // READ
    suspend fun read(): List<ReadProduto> {
        return dbQuery {
            Produto.selectAll().map { row ->
                ReadProduto(
                    row[Produto.id],
                    row[Produto.nome],
                    row[Produto.preco],
                    row[Produto.quant_estoque],
                    row[Produto.categoria],
                    row[Produto.caracteristas],
                    row[Produto.avaliacao_geral],
                    row[Produto.imagem_url],
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadProduto? {
        return dbQuery {
            Produto.select {Produto.id eq id }
                .map {
                    ReadProduto(
                        it[Produto.id],
                        it[Produto.nome],
                        it[Produto.preco],
                        it[Produto.quant_estoque],
                        it[Produto.categoria],
                        it[Produto.caracteristas],
                        it[Produto.avaliacao_geral],
                        it[Produto.imagem_url]
                    )
                }.singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, produto: ReadProduto) {
        dbQuery {
            Produto.update({ Produto.id eq id }) {
                it[nome] = produto.nome
                it[preco] = produto.preco
                it[quant_estoque] = produto.quant_estoque
                it[categoria] = produto.categoria
                it[caracteristas] = produto.caracteristas
                it[avaliacao_geral] = produto.avaliacao_geral
                it[imagem_url] = produto.imagem_url
            }
        }
    }

    // DELETE
    suspend fun deleteProduto(id: Int): Int = dbQuery {
        Produto.deleteWhere { Produto.id eq id }
    }


}