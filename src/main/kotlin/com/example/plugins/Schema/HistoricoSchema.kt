package com.example.plugins.Schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Serializable
data class ExposedHistorico(val id_cliente: Int, val id_produto: Int)
@Serializable
data class ReadHistorico(val id: Int, val id_cliente: Int, val id_produto: Int)


class HistoricoSchema(private val database: Database) {
    object Historico : Table() {
        val id = integer("id").autoIncrement()
        val id_cliente = integer("id_endereco")
        val id_produto = integer("id_produto")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedHistorico): Int = dbQuery {
        Historico.insert {
            it[id_cliente] = insert.id_cliente
            it[id_produto] = insert.id_produto
        }[Historico.id]
    }

    // READ
    suspend fun read(): List<ReadHistorico> {
        return dbQuery {
            Historico.selectAll().map { row ->
                ReadHistorico(
                    row[Historico.id],
                    row[Historico.id_cliente],
                    row[Historico.id_produto],
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadHistorico? {
        return dbQuery {
            Historico.select { Historico.id eq id }
                .map {
                    ReadHistorico(
                        it[Historico.id],
                        it[Historico.id_cliente],
                        it[Historico.id_produto],
                    )
                }
                .singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, historico: ReadHistorico) {
        dbQuery {
            Historico.update({ Historico.id eq id }) {
                it[id_cliente]= historico.id_cliente
                it[id_produto]= historico.id_produto
            }
        }
    }

    // DELETE
    suspend fun deleteHistorico(id: Int): Int = dbQuery {
        Historico.deleteWhere { Historico.id eq id }
    }


}