package com.sodovaya.sb_rewriter
import com.google.gson.Gson
import modelsRequests.Instance
import modelsRequests.requestJSON
import modelsResponse.sberResponse
import okhttp3.*
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

const val api = "https://api.aicloud.sbercloud.ru/public/v2/rewriter/predict"
val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")

fun main() {
    println("Connecting to Sberbank API")
    connectAndGet()
}

fun connectAndGet() {
    val client = OkHttpClient()

    val fileText = File("text.txt")
    val textToSend = fileText.readText().trim().replace("\n", " ").replace("\"", "\'")
    val postJSON = requestJSON(listOf(Instance(text = textToSend, temperature = 0.9, top_k = 50, top_p = 0.7, range_mode = "bertscore")))

    val formBody = RequestBody.create(JSON, Gson().toJson(postJSON))

    val request: Request = Request.Builder()
        .url(api)
        .post(formBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("request failure"); return
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    println("response isn't successful"); return
                }

                val inpStream = response.body()?.string()
                if (inpStream != null) {
                    println("ok")
                    toJson(inpStream)
                }
                return
            }
        }
    })
}

fun toJson(response: String) {
    val sberResponse = Gson().fromJson(response, sberResponse::class.java)

    var predicted = ""
    for (i in sberResponse.predictions_all.indices) {
        val j = i + 1
        predicted = predicted + j + ". " + sberResponse.predictions_all[i] + "\n\n"
    }

    val output = File("output.txt")
    output.writeText(sberResponse.prediction_best.bertscore + "\n\n\n" + predicted)
    exitProcess(0)
}
