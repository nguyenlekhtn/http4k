package com.hung

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

data class Student(
    val id: Int,
    val name: String,
    val className: String
)

object StudentDb {
    private val userDb = mutableListOf(
        Student(id = 1, name = "Jim", "DI1796A1"),
        Student(id = 2, name = "Bob", "DI1796A1"),
        Student(id = 3, name = "Sue", "DI1796A1"),
        Student(id = 4, name = "Rita", "DI1796A1"),
        Student(id = 5, name = "Charlie", "DI1796A1")
    )

    fun search(vararg ids: Int) = userDb.filter { ids.contains(it.id) }
    fun delete(vararg ids: Int) = userDb.removeIf { ids.contains(it.id) }
}

fun JsonApi(student: Student): HttpHandler {
    val bodyLens = Body.auto<List<Student>>().toLens()

    return routes(
        "/student" bind GET to {
            Response(OK).with(bodyLens of StudentDb.search(1))
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
