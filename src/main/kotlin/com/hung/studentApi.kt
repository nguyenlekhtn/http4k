package com.hung

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import sun.invoke.empty.Empty

data class Student(
    val id: Int,
    val name: String,
    val className: String
)

object StudentDb {
    private val userDb = mutableListOf(
        Student(id = 1, name = "Jim", "DI1796A2"),
        Student(id = 2, name = "Bob", "DI1796A1"),
        Student(id = 3, name = "Sue", "DI1796A2"),
        Student(id = 4, name = "Jim", "DI1796A1"),
        Student(id = 5, name = "Charlie", "DI1796A2")
    )

    fun searchById(id: Int?): List<Student> {
        if(id != null){
            return userDb.filter { it.id == id }
        }
        return emptyList()
    }
    fun search(name: String?, className: String?): List<Student> {
        var result: List<Student> = userDb
        if(name == null && className == null){
            return emptyList()
        }
        if(name != null){
            result = result.filter { it.name == name }
        }
        if(className != null){
            result = result.filter { it.className == className }
        }
        return result
    }
}

fun JsonApi(student: Student): HttpHandler {
    val bodyLens = Body.auto<List<Student>>().toLens()

    return routes(
        "/student" bind GET to { request: Request ->
            val name = request.query("name") ?: null
            val className = request.query("className") ?: null
            Response(OK).with(bodyLens of StudentDb.search(name, className))
        },
        "/student/{id:.*}" bind GET to { request: Request ->
            val id = request.path("id")
            Response(OK).with( bodyLens of StudentDb.searchById(id?.toInt()))
        },
        "/student" bind POST to {
            val received = bodyLens(it)
            Response(CREATED).with(bodyLens of received)
        },
        "/api" bind routes(
            "/{name:.*}" bind GET to { request:Request -> Response(OK).body("Hello, ${request.path("name")}!") }
        )
    )
}


fun main(){
    val student = Student(1, "Trieu Tu Vong", "DI1796A1")
    JsonApi(student).asServer(SunHttp(8080)).start()
}
