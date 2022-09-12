package modelsResponse

data class sberResponse(
    val origin: String,
    val prediction_best: PredictionBest,
    val predictions_all: List<String>
)