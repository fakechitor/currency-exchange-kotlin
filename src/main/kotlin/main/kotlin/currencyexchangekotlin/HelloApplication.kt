package main.kotlin.currencyexchangekotlin

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

@ApplicationPath("/api")
class HelloApplication : Application() {
    private var test = "test"
}