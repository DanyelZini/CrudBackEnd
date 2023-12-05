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
data class ExposedCarrinho(val id_cliente: Int, val data_dia: Int, val data_mes: Int, val data_ano: Int, val status_pedido: Int)
@Serializable
data class ReadCarrinho(val id: Int, val id_cliente: Int, val data_dia: Int, val data_mes: Int, val data_ano: Int, val status_pedido: Int)


class CarrinhoSchema(private val database: Database) {
    object Carrinho : Table() {
        val id = integer("id").autoIncrement()
        val id_cliente = integer("id_cliente")
        val data_dia = integer("data_dia").check {(it greaterEq 1) and (it lessEq 31)}
        val data_mes = integer("data_mes").check {(it greaterEq 1) and (it lessEq 12)}
        val data_ano = integer("data_ano")
        val status_pedido = integer("status_pedido").check {((it greaterEq 0) and (it lessEq 4))}

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedCarrinho): Int = dbQuery {
        Carrinho.insert {
            it[id_cliente] = insert.id_cliente
            it[data_dia] = insert.data_dia
            it[data_mes] = insert.data_mes
            it[data_ano] = insert.data_ano
            it[status_pedido] = insert.status_pedido
        }[Carrinho.id]
    }

    // READ
    suspend fun read(): List<ReadCarrinho> {
        return dbQuery {
            Carrinho.selectAll().map { row ->
                ReadCarrinho(
                    row[Carrinho.id],
                    row[Carrinho.id_cliente],
                    row[Carrinho.data_dia],
                    row[Carrinho.data_mes],
                    row[Carrinho.data_ano],
                    row[Carrinho.status_pedido],
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadCarrinho? {
        return dbQuery {
            Carrinho.select { Carrinho.id_cliente eq id }
                .map {
                    ReadCarrinho(
                        it[Carrinho.id],
                        it[Carrinho.id_cliente],
                        it[Carrinho.data_dia],
                        it[Carrinho.data_mes],
                        it[Carrinho.data_ano],
                        it[Carrinho.status_pedido]
                    )
                }
                .singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, carrinho: ExposedCarrinho) {
        dbQuery {
            Carrinho.update({ Carrinho.id_cliente eq id }) {
                it[id_cliente] = carrinho.id_cliente
                it[data_dia] = carrinho.data_dia
                it[data_mes] = carrinho.data_mes
                it[data_ano] = carrinho.data_ano
                it[status_pedido] = carrinho.status_pedido
            }
        }
    }

    // DELETE
    suspend fun deleteCarrinho(id: Int): Int = dbQuery {
        Carrinho.deleteWhere { Carrinho.id_cliente eq id }
    }


}