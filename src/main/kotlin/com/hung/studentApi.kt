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

data class JsonMessage(
    val id: Int,
    val name: String,
    val className: String
)

fun JsonApi(student: JsonMessage): HttpHandler {
    val bodyLens = Body.auto<JsonMessage>().toLens()

    return routes(
        "/student" bind GET to {
            Response(OK).with(bodyLens of student)
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
    val student = JsonMessage(1, "Trieu Tu Vong", "DI1796A1")
    JsonApi(student).asServer(SunHttp(8080)).start()
}
