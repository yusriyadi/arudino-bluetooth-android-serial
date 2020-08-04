package tellabs.android.basekotlin.utils

import com.google.gson.Gson


fun formatRupiah(angka: Double): String =
    String.format("Rp. %,.0f", angka).replace(",".toRegex(), ".")

fun objectToJson(objectClass: Any): String {
    return Gson().toJson(objectClass)
}

inline fun <reified T> jsonToObject(json: String): T {
    return Gson().fromJson(json, T::class.java)
}

//
//fun getErrorMessage(e: Exception): String {
//
//    var errorMsg = ""
//
//    if (e is HttpException) {
//        val body = e.response()?.errorBody()
//        val errorResponse = parseError(body)
//        errorResponse?.let {
//            it.metaData?.let { metaData ->
//                errorMsg = metaData.message
//            }
//        }
//    } else {
//        errorMsg = "Terjadi Kesalahan"
//    }
//    return errorMsg
//}
//
//
//
//fun getErrorMessage(e: Throwable): String {
//
//    var errorMsg = ""
//
//    if (e is HttpException) {
//        val body = e.response()?.errorBody()
//        val errorResponse = parseError(body)
//        errorResponse?.let {
//            it.metaData?.let { metaData ->
//                errorMsg = metaData.message
//            }
//        }
//    } else {
//        errorMsg = "Terjadi Kesalahan"
//    }
//
//    return errorMsg
//}
//
//
//fun parseError(body: ResponseBody?): ErrorResponse? {
//
//    val adapter = Gson().getAdapter(ErrorResponse::class.java)
//    return try {
//        adapter.fromJson(body?.string())
//
//    } catch (e: IOException) {
//        e.printStackTrace()
//        null
//    }
//}
