package com.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun HelloWorld(): HttpHandler {
    return routes("/" bind GET to { Response(OK).body("hello http4k")})
}

fun main() {
    HelloWorld().asServer(SunHttp(8080)).start()
}