package wallapp.pixel.render

import wallapp.pixel.alert.AlertManagerComposable
import wallapp.pixel.shape.ShapeMapperComposable


val Render.shapeMapperComposable: ShapeMapperComposable
    get() = shapeMapper as ShapeMapperComposable

val Render.alertManagerComposable: AlertManagerComposable
    get() = alertManager as AlertManagerComposable
