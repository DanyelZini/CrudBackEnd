package com.example.plugins

import com.example.plugins.Schema.*
import io.ktor.http.*
//import com.example.plugins.Schema.ClienteService.clientes.nome
//import com.example.plugins.Schema.EnderecoService
//import com.example.plugins.Schema.ExposedCliente
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*


fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:sqlite:identifier.sqlite",
        driver = "org.sqlite.JDBC"
    )

    val clienteSchema = ClienteSchema(database)
    val enderecoSchema = EnderecoSchema(database)
    val carrinhoSchema = CarrinhoSchema(database)
    val produtoSchema = ProdutoSchema(database)
    val itensCarSchema = ItensCarSchema(database)
    val comentarioSchema = ComentarioSchema(database)
    val historicoSchema = HistoricoSchema(database)


    routing {

        // ====================== CLIENTE ====================== //
        // PEGAR TODOS OS CLIENTES
        get("/cliente") {
            val clientes = clienteSchema.read()
            if (clientes != null) {
                call.respond(HttpStatusCode.OK, clientes)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // PEGAR CLIENTE DO ID
        get("/cliente/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val cliente = clienteSchema.readUnit(id)
            if (cliente != null) {
                call.respond(HttpStatusCode.OK, cliente)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // CRIAR CLIENTE
        post("/cliente") {
            val input = call.receive<ExposedClientes>()
            clienteSchema.create(input)
            call.respond(HttpStatusCode.Created)
        }

        // ATUALIZAR CLIENTE DO ID
        put("/cliente/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val cliente = call.receive<ExposedClientes>()
            clienteSchema.update(id, cliente)
            call.respond(HttpStatusCode.OK)
        }

        // ATUALIZAR CLIENTE DO ID
        patch("/cliente/{id}") {

        }

        // DELETAR CLIENTE DO ID
        delete("/cliente/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            clienteSchema.deleteClientes(id)
        }

        // LOGIN CLIENTE USANDO EMAIL E SENHA
        get("/cliente/login") {
            val input = call.receive<LoginClientes>()
            val cliente = clienteSchema.loginClientes(input)
            if (cliente != 0 && cliente != null) {
                call.respond(HttpStatusCode.OK, "ID DO CLIENTE VALIDO, "+cliente)
            } else {
                call.respond(HttpStatusCode.NotFound, "EMAIL OU SENHA NAO ENCONTRADO OU INVALIDO")
            }
        }

        // ====================== ENDERECO ====================== //
        // PEGAR TODOS OS ENDERECO
        get("/endereco") {
            val endereco = enderecoSchema.read()
            if (endereco != null) {
                call.respond(HttpStatusCode.OK, endereco)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // PEGAR ENDERECO DO ID
        get("/endereco/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val endereco = enderecoSchema.readUnit(id)
            if (endereco != null) {
                call.respond(HttpStatusCode.OK, endereco)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR ENDERECO
        post("/endereco/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val input = call.receive<ExposedEndereco>()
            val id_endereco = enderecoSchema.create(input, id)
            clienteSchema.update_endereco(id, id_endereco)
            call.respond(HttpStatusCode.Created, "ID DO CLIENTE = "+id+", ID DO ENDERECO ADICIONADO NA VARIAVEL ID_ENDERECO DO CLIENTE CORRESPONDENTE "+id_endereco)
        }

        // ATUALIZAR ENDERECO DO ID
        put("/endereco/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val endereco = call.receive<ExposedEndereco>()
            enderecoSchema.update(id, endereco)
            call.respond(HttpStatusCode.OK)
        }

        // ATUALIZAR ENDERECO DO ID
        patch("/endereco/{id}") {

        }

        // DELETAR ENDERECO DO ID
        delete("/endereco/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            enderecoSchema.deleteEndereco(id)
        }

        // ====================== CARRINHO ====================== //
        // PEGAR TODOS OS CARRINHO
        get("/carrinho") {
            val carrinho = carrinhoSchema.read()
            if (carrinho != null) {
                call.respond(HttpStatusCode.OK, carrinho)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // PEGAR CARRINHO DO ID
        get("/carrinho/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val carrinho = carrinhoSchema.readUnit(id)
            if (carrinho != null) {
                call.respond(HttpStatusCode.OK, carrinho)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR CARRINHO
        post("/carrinho") {
            val input = call.receive<ExposedCarrinho>()
            carrinhoSchema.create(input)
            call.respond(HttpStatusCode.Created)
        }

        // ATUALIZAR CARRINHO DO ID
        put("/carrinho/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val carrinho = call.receive<ExposedCarrinho>()
            carrinhoSchema.update(id,carrinho)
            call.respond(HttpStatusCode.OK)
        }

        // DELETAR CARRINHO DO ID
        delete("/carrinho/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            carrinhoSchema.deleteCarrinho(id)
        }

        // ====================== PRODUTO ====================== //
        // PEGAR TODOS OS PRODUTO
        get("/produto") {
            val produto = produtoSchema.read()
            if (produto != null) {
                call.respond(HttpStatusCode.OK, produto)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // PEGAR PRODUTO DO ID
        get("/produto/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val produto = enderecoSchema.readUnit(id)
            if (produto != null) {
                call.respond(HttpStatusCode.OK, produto)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR PRODUTO
        post("/produto") {
            val input = call.receive<ExposedProduto>()
            val id = produtoSchema.create(input)
            call.respond(HttpStatusCode.Created, id)
        }

        // ATUALIZAR PRODUTO DO ID
        put("/produto/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val produto = call.receive<ReadProduto>()
            produtoSchema.update(id, produto)
            call.respond(HttpStatusCode.OK)
        }

        // DELETAR PRODUTO DO ID
        delete("/produto/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            produtoSchema.deleteProduto(id)
        }

        // ====================== ITENS DO CARRINHO ====================== //
        // PEGAR TODOS OS ITENS DO CARRINHO
        get("/itenscar") {
            val itenscar = itensCarSchema.read()
            if (itenscar != null) {
                call.respond(HttpStatusCode.OK, itenscar)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // PEGAR ITENS DO CARRINHO DO ID
        get("/itenscar/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val itenscar = itensCarSchema.readUnit(id)
            if (itenscar != null) {
                call.respond(HttpStatusCode.OK, itenscar)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR ITENS DO CARRINHO
        post("/itenscar") {
            val input = call.receive<ExposedItens>()
            itensCarSchema.create(input)
            call.respond(HttpStatusCode.Created)
        }

        // ATUALIZAR ITENS DO CARRINHO DO ID
        put("/itenscar/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val itenscar = call.receive<ExposedItens>()
            itensCarSchema.update(id,itenscar)
            call.respond(HttpStatusCode.OK)
        }

        // DELETAR ITENS DO CARRINHO DO ID
        delete("/itenscar/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            itensCarSchema.deleteItens(id)
        }

        // ====================== COMENTARIOS ====================== //
        // PEGAR TODOS OS COMENTARIOS
        get("/comentarios") {
            val comentarios = comentarioSchema.read()
            if (comentarios != null) {
                call.respond(HttpStatusCode.OK, comentarios)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // PEGAR COMENTARIO DO ID
        get("/comentarios/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val comentario = comentarioSchema.readUnit(id)
            if (comentario != null) {
                call.respond(HttpStatusCode.OK, comentario)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR COMENTARIO
        post("/comentarios") {
            val input = call.receive<ExposedComentario>()
            comentarioSchema.create(input)
            call.respond(HttpStatusCode.Created)
        }

        // ATUALIZAR COMENTARIO DO ID
        put("/comentarios/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val comentario = call.receive<ReadComentario>()
            comentarioSchema.update(id, comentario)
            call.respond(HttpStatusCode.OK)
        }

        // DELETAR COMENTARIO DO ID
        delete("/comentarios/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            comentarioSchema.deleteComentario(id)
        }

        // ====================== HISTORICO ====================== //
        // PEGAR TODOS OS HISTORICO
        get("/historico") {
            val historico = historicoSchema.read()
            if (historico != null) {
                call.respond(HttpStatusCode.OK, historico)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // PEGAR HISTORICO DO ID
        get("/historico/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val historico = historicoSchema.readUnit(id)
            if (historico != null) {
                call.respond(HttpStatusCode.OK, historico)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // CRIAR HISTORICO
        post("/historico") {
            val input = call.receive<ExposedHistorico>()
            historicoSchema.create(input)
            call.respond(HttpStatusCode.Created)
        }

        // ATUALIZAR HISTORICO DO ID
        put("/historico/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val historico = call.receive<ReadHistorico>()
            historicoSchema.update(id, historico)
            call.respond(HttpStatusCode.OK)
        }

        // DELETAR HISTORICO DO ID
        delete("/historico/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            historicoSchema.deleteHistorico(id)
        }
    }
}