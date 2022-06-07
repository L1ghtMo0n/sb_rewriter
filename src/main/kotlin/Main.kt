import okhttp3.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

const val api = "https://api.aicloud.sbercloud.ru/public/v2/rewriter/predict"
val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")

fun main() {
    println("Connecting to Sberbank API")
    connectAndGet()
}

fun connectAndGet(){
    val client = OkHttpClient()

    val file = File("config.txt")
    var cont = file.readText()
    val fileText = File("text.txt")
    val contText = fileText.readText().trim()
    cont = cont.replace("txtxt", contText)
    val formBody = RequestBody.create(JSON, cont)

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
                if (!response.isSuccessful) {println("response isn't successful"); return}

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

fun toJson(response: String){
    val jsonParser = JSONParser()
    val root = jsonParser.parse(response) as JSONObject
    val predictionBest = root["prediction_best"] as JSONObject
    val bertscore = predictionBest["bertscore"] as String
    val predictionAll = root["predictions_all"] as JSONArray

    var predicted = ""
    for (i in predictionAll.indices){
        val j = i + 1
        predicted = predicted + j + ". " + predictionAll[i] + "\n\n"
    }

    val output = File("output.txt")
    output.writeText(bertscore + "\n\n\n" + predicted)
    exitProcess(0)
}


