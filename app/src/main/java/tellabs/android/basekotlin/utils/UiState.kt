package tellabs.android.basekotlin.utils


/*ini adalah kelas state yang digunakan pada ui untuk menandai state pada request data
* terdiri dari state loading
* state success dan membawa data object generic
* error pun idem
* */
sealed class UiState<T> {
    data class Loading<T>(val isLoading: Boolean = true) : UiState<T>()
    data class Success<T>(val data : T) : UiState<T>()
    data class Error<T>(val throwable: Throwable) : UiState<T>()
}