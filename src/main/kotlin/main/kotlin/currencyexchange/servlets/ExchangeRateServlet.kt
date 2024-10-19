package main.kotlin.currencyexchange.servlets

import com.google.gson.Gson
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import main.kotlin.currencyexchange.service.ExchangeService

@WebServlet(name = "exchangeRate", value = ["/exchangeRate/*"])
class ExchangeRateServlet : HttpServlet() {
    private val exchangeService = ExchangeService()
    private val gson = Gson()

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        if (req.method.equals("PATCH", ignoreCase = true)) {
            doPatch(req, resp)
        } else {
            super.service(req, resp)
        }
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val connectedCodes = req.pathInfo.trim('/')
        try {
            if (connectedCodes.length == 6) {
                val exchangeRate = exchangeService.getByCode(connectedCodes)
                resp.contentType = "application/json"
                val printWriter = resp.writer
                val jsonResponse = gson.toJson(exchangeRate)
                printWriter.write(jsonResponse)

            } else {
                resp.status = HttpServletResponse.SC_BAD_REQUEST
                val answer = mapOf("message" to "Некорректный ввод")
                val jsonResponse = gson.toJson(answer)
                resp.writer.write(jsonResponse)
            }
        }
        catch (e: IllegalArgumentException) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            val answer = mapOf("message" to "Валюта не найдена")
            val jsonResponse = gson.toJson(answer)
            resp.writer.write(jsonResponse)
        }
        catch (e: Exception) {
            e.printStackTrace()
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            val answer = mapOf("message" to "Ошибка внутреннего сервера")
            val jsonResponse = gson.toJson(answer)
            resp.writer.write(jsonResponse)
        }
    }

    override fun doPatch(req: HttpServletRequest, resp: HttpServletResponse) {
        val rate = req.getParameter("rate").toDouble()
        val connectedCodes = req.pathInfo.trim('/')
        try {
            if (connectedCodes.length == 6) {
                resp.contentType = "application/json"
                val exchangeRate = exchangeService.patchRate(connectedCodes, rate)
                val responseJson = gson.toJson(exchangeRate)
                resp.writer.write(responseJson)
                resp.status = HttpServletResponse.SC_OK
            } else {
                resp.status = HttpServletResponse.SC_BAD_REQUEST
                val answer = mapOf("message" to "Валюта не найдена")
                val jsonResponse = gson.toJson(answer)
                resp.writer.write(jsonResponse)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            val answer = mapOf("message" to "Ошибка внутреннего сервера")
            val jsonResponse = gson.toJson(answer)
            resp.writer.write(jsonResponse)
        }
    }
}