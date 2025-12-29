package wallapp.preference


interface MutableObservableValue<T> : ObservableValue<T> {
    fun update(newValue: T)
    fun updateIfNew(newValue: T)
}