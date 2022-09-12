package modelsRequests

data class Instance(
    val range_mode: String,
    val temperature: Double,
    val text: String,
    val top_k: Int,
    val top_p: Double
)