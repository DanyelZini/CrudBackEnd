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
data class ExposedItens(val id_pedido: Int, val id_produto: Int, val quant: Int, val preco_unit: Float)
@Serializable
data class ReadItens(val id: Int, val id_pedido: Int, val id_produto: Int, val quantidade: Int, val preco_unit: Float)

class ItensCarSchema(private val database: Database) {
    object ItensCar : Table() {
        val id = integer("id").autoIncrement()
        val id_pedido = integer("id_pedido")
        val id_produto = integer("id_produto")
        val quant = integer("quant").check {(it greaterEq 1)}
        val preco_unit = float("preco_unit").check {(it greaterEq 0)}

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedItens): Int = dbQuery {
        ItensCar.insert {
            it[id_pedido] = insert.id_pedido
            it[id_produto] = insert.id_produto
            it[quant] = insert.quant
            it[preco_unit] = insert.preco_unit
        }[ItensCar.id]
    }

    // READ
    suspend fun read(): List<ReadItens> {
        return dbQuery {
            ItensCar.selectAll().map { row ->
                ReadItens(
                    row[ItensCar.id],
                    row[ItensCar.id_pedido],
                    row[ItensCar.id_produto],
                    row[ItensCar.quant],
                    row[ItensCar.preco_unit],
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadItens? {
        return dbQuery {
            ItensCar.select { ItensCar.id_pedido eq id }
                .map {
                    ReadItens(
                        it[ItensCar.id],
                        it[ItensCar.id_pedido],
                        it[ItensCar.id_produto],
                        it[ItensCar.quant],
                        it[ItensCar.preco_unit]
                    )
                }
                .singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, itensCar: ExposedItens) {
        dbQuery {
            ItensCar.update({ ItensCar.id_pedido eq id }) {
                it[id_pedido] = itensCar.id_pedido
                it[id_produto] = itensCar.id_produto
                it[quant] = itensCar.quant
                it[preco_unit] = itensCar.preco_unit
            }
        }
    }

    // DELETE
    suspend fun deleteItens(id: Int): Int = dbQuery {
        ItensCar.deleteWhere { ItensCar.id_pedido eq id }
    }


}