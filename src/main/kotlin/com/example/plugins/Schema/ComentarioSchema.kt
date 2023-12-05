package com.example.plugins.Schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope

@Serializable
data class ExposedComentario(val id_cliente: Int, val id_produto: Int, val coment: String, val avaliacao: Float)
@Serializable
data class ReadComentario(val id: Int, val id_cliente: Int, val id_produto: Int, val coment: String, val avaliacao: Float)


class ComentarioSchema(private val database: Database) {
    object Comentario : Table() {
        val id = integer("id").autoIncrement()
        val id_cliente = integer("id_cliente")
        val id_produto = integer("id_produto")
        val coment = varchar("coment", 200)
        val avaliacao = float("avaliacao").check {(it greaterEq 0) and (it lessEq 5)}

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedComentario): Int = dbQuery {
        Comentario.insert {
            it[id_cliente] = insert.id_cliente
            it[id_produto] = insert.id_produto
            it[coment] = insert.coment
            it[avaliacao] = insert.avaliacao
        }[Comentario.id]
    }

    // READ
    suspend fun read(): List<ReadComentario> {
        return dbQuery {
            Comentario.selectAll().map { row ->
                ReadComentario(
                    row[Comentario.id],
                    row[Comentario.id_cliente],
                    row[Comentario.id_produto],
                    row[Comentario.coment],
                    row[Comentario.avaliacao],
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadComentario? {
        return dbQuery {
            Comentario.select {Comentario.id eq id }
                .map {
                    ReadComentario(
                        it[Comentario.id],
                        it[Comentario.id_cliente],
                        it[Comentario.id_produto],
                        it[Comentario.coment],
                        it[Comentario.avaliacao]
                    )
                }
                .singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, comentario: ReadComentario) {
        dbQuery {
            Comentario.update({ Comentario.id eq id }) {
                it[id_cliente] = comentario.id_cliente
                it[id_produto] = comentario.id_produto
                it[coment] = comentario.coment
                it[avaliacao] = comentario.avaliacao
            }
        }
    }

    // DELETE
    suspend fun deleteComentario(id: Int): Int = dbQuery {
        Comentario.deleteWhere { Comentario.id eq id }
    }


}