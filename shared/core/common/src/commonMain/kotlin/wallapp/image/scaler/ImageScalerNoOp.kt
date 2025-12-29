package wallapp.image.scaler

object ImageScalerNoOp : ImageScaler() {

    override val density: Float
        get() = 2f
    override val widthScale: Float
        get() = 1f
    override val heightScale: Float
        get() = 1f

}