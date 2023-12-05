package com.example.plugins.Schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.example.plugins.Schema.*

@Serializable
data class ExposedEndereco(val pais: String, val rua: String, val cidade: String, val estado: String, val cep: String, val numero: Int, val complemento: String, val ponto_ref: String)
@Serializable
data class ReadEndereco(val id: Int, val pais: String, val rua: String, val cidade: String, val estado: String, val cep: String, val numero: Int, val complemento: String, val ponto_ref: String)


class EnderecoSchema(private val database: Database) {
    object Enderecos : Table() {
        val id = integer("id").autoIncrement()
        val pais = varchar("pais", 100)
        val rua = varchar("rua", 100)
        val cidade = varchar("cidade", 100)
        val estado = varchar("estado", 2)
        val cep = varchar("cep", 9)
        val numero = integer("numero")
        val complemento = varchar("complemento", 20)
        val ponto_ref = varchar("ponto_ref", 200)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
    // CREATE
    suspend fun create(endereco : ExposedEndereco, id: Int) : Int = dbQuery {
         Enderecos.insert {
            it[pais] = endereco.pais
            it[rua] = endereco.rua
            it[cidade] = endereco.cidade
            it[estado] = endereco.estado
            it[cep] = endereco.cep
            it[numero] = endereco.numero
            it[complemento] = endereco.complemento
            it[ponto_ref] = endereco.ponto_ref
        }[Enderecos.id]

    }

    suspend fun read(): List<ReadEndereco> {
        return dbQuery {
            Enderecos.selectAll().map { row ->
                ReadEndereco(
                    row[Enderecos.id],
                    row[Enderecos.pais],
                    row[Enderecos.rua],
                    row[Enderecos.cidade],
                    row[Enderecos.estado],
                    row[Enderecos.cep],
                    row[Enderecos.numero],
                    row[Enderecos.complemento],
                    row[Enderecos.ponto_ref]
                )
            }
        }
    }

    // READ ID
    suspend fun readUnit(id: Int): ExposedEndereco? {
        return dbQuery {
            Enderecos.select { Enderecos.id eq id}
                .map { ExposedEndereco(it[Enderecos.pais], it[Enderecos.rua], it[Enderecos.cidade], it[Enderecos.estado], it[Enderecos.cep] , it[Enderecos.numero], it[Enderecos.complemento], it[Enderecos.ponto_ref])}
                .singleOrNull()
        }
    }

    // UPDATE ID
    suspend fun update(id: Int, endereco: ExposedEndereco) {
        dbQuery {
            EnderecoSchema.Enderecos.update({ EnderecoSchema.Enderecos.id eq id}) {
                it[pais] = endereco.pais
                it[rua] = endereco.rua
                it[cidade] = endereco.cidade
                it[estado] = endereco.estado
                it[cep] = endereco.cep
                it[numero] = endereco.numero
                it[complemento] = endereco.complemento
                it[ponto_ref] = endereco.ponto_ref
            }
        }
    }

    suspend fun deleteEndereco(id: Int) : Int = dbQuery {
        Enderecos.deleteWhere {Enderecos.id eq id}
    }



}

/*
@Serializable
data class ExposedEndereco(val pais: String, val rua: String, val cidade: String, val estado: String, val cep: String, val numero: Int, val complemento: String, val ponto_ref: String)
class EnderecoService(private val database: org.jetbrains.exposed.sql.Database) {
    object enderecos : Table() {
        val cliente_id = integer("cliente_id")
        val pais = varchar("pais", 200)
        val rua = varchar("rua", 200)
        val cidade = varchar("cidade", 100)
        val estado = varchar("estado", 2)
        val cep = varchar("cep", 10)
        val numero = integer("numero")
        val complemento = varchar("complemento", 10)
        val ponto_ref = varchar("ponto_ref", 200)
    }
}

 */
