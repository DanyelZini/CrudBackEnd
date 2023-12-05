package com.example.plugins.Schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope

@Serializable
data class ExposedClientes(val nome: String, val email: String, val senha: String, val telefone: String, val cpf: String)
@Serializable
data class ReadClientes(val id: Int, val id_endereco: Int, val nome: String, val email: String, val senha: String, val telefone: String, val cpf: String)
@Serializable
data class LoginClientes(val email: String, val senha: String)
@Serializable
data class ReadIdClientes(val id: Int)

class ClienteSchema(private val database: Database) {
    object Clientes : Table() {
        val id = integer("id").autoIncrement()
        val id_endereco = integer("id_endereco").default(-1)
        val nome = varchar("nome", 100)
        val email = varchar("email", 100)
        val senha = varchar("senha", 100)
        val telefone = varchar("telefone", 20)
        val cpf = varchar("cpf", 11)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    // CREATE
    suspend fun create(insert: ExposedClientes): Int = dbQuery {
        Clientes.insert {
            it[nome] = insert.nome
            it[email] = insert.email
            it[senha] = insert.senha
            it[telefone] = insert.telefone
            it[cpf] = insert.cpf
        }[Clientes.id]
    }

    // READ
    suspend fun read(): List<ReadClientes> {
        return dbQuery {
            Clientes.selectAll().map { row ->
                ReadClientes(
                    row[Clientes.id],
                    row[Clientes.id_endereco],
                    row[Clientes.nome],
                    row[Clientes.email],
                    row[Clientes.senha],
                    row[Clientes.telefone],
                    row[Clientes.cpf]
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ReadClientes? {
        return dbQuery {
            Clientes.select { Clientes.id eq id }
                .map {
                    ReadClientes(
                        it[Clientes.id],
                        it[Clientes.id_endereco],
                        it[Clientes.nome],
                        it[Clientes.email],
                        it[Clientes.senha],
                        it[Clientes.telefone],
                        it[Clientes.cpf]
                    )
                }
                .singleOrNull()
        }
    }

    // UPDATE
    suspend fun update(id: Int, cliente: ExposedClientes) {
        dbQuery {
            Clientes.update({ Clientes.id eq id }) {
                it[nome] = cliente.nome
                it[email] = cliente.email
                it[senha] = cliente.senha
                it[telefone] = cliente.telefone
                it[cpf] = cliente.cpf
            }
        }
    }

    // DELETE
    suspend fun deleteClientes(id: Int): Int = dbQuery {
        Clientes.deleteWhere { Clientes.id eq id }
    }

    suspend fun verificaLogin(clientes: LoginClientes): Boolean {
        var credenciais = false
        transaction {
            val resultado = Clientes.select {
                (Clientes.email eq clientes.email) and (Clientes.senha eq clientes.senha)
            }.count()
            credenciais = resultado > 0
        }
        return credenciais
    }

    suspend fun loginClientes(clientes: LoginClientes): Int? {
        if (verificaLogin(clientes)) {
            return dbQuery {
                Clientes.select {
                    (Clientes.email eq clientes.email) and (Clientes.senha eq clientes.senha)
                }.map {
                    it[Clientes.id]
                }.singleOrNull()
            }
        }
        return null
    }

    suspend fun update_endereco(id_cliente: Int, id_endereco: Int) {
        dbQuery {
            Clientes.update({ Clientes.id eq id_cliente }) {
                it[Clientes.id_endereco] = id_endereco
            }
        }
    }

}



/*
@Serializable
data class ExposedCliente(val nome: String, val email: String, val senha: String, val telefone: String, val cpf: String)
class ClienteService(private val database: org.jetbrains.exposed.sql.Database) {
    object clientes : IntIdTable() {
        val nome = varchar("nome", 100)
        val email = varchar("email", 100)
        val senha = varchar("senha", 100)
        val telefone = varchar("telefone", 20)
        val cpf = varchar("cpf", 11)
    }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


 */
//    suspend fun create(cliente : ExposedCliente) {
//        cliente.insertAndGetId {
//            it[nome] = cliente.nome
//            it[email] = cliente.email
//            it[senha] = cliente.senha
//            it[telefone] = cliente.telefone
//            it[cpf] = cliente.cpf
//        }
