package wallapp.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import wallapp.pixel.render.Render


sealed interface ScreenTransition {

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?

    companion object {
        private const val defaultDuration: Int = 300
    }

    class ScreenTransitionNone(
        private val label: String,
    ) : ScreenTransition {
        override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
            get() = {
//                Log.v("[$label] ScreenTransitionNone.enterTransition()")
                EnterTransition.None
            }

        override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
            get() = {
//                Log.v("[$label] ScreenTransitionNone.exitTransition()")
                ExitTransition.None
            }

        override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)?
            get() = {
//                Log.v("[$label] ScreenTransitionNone.popEnterTransition()")
                EnterTransition.None
            }

        override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)?
            get() = {
//                Log.v("[$label] ScreenTransitionNone.popExitTransition()")
                ExitTransition.None
            }
    }

    /**
     *
     */
    class ScreenTransitionFade(
        private val label: String,
        private val duration: Int = defaultDuration,
    ) : ScreenTransition {

        override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionFade.enterTransition()")
                fadeIn(animationSpec = tween(duration))
            }

        override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionFade.exitTransition()")
                fadeOut(animationSpec = tween(duration))
            }

        override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
            get() {
//                Log.v("[$label] ScreenTransitionFade.popEnterTransition()")
                return enterTransition
            }

        override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
            get() {
//                Log.v("[$label] ScreenTransitionFade.popExitTransition()")
                return exitTransition
            }
    }

    class ScreenTransitionSlideVertically(
        private val render: Render,
        private val label: String,
    ) : ScreenTransition {

        private val enterOffsetY: Int
            get() = render.windowFrame.deviceHeightPx
        private val enterAnimationSpec: FiniteAnimationSpec<IntOffset> = spring(
            stiffness = Spring.StiffnessLow,
        )

        // Add a status bar height to the exit offset to prevent the animation from visually hanging
        // around at the bottom of the screen. See #2358.
        private val exitOffsetY: Int
            get() = render.windowFrame.deviceHeightPx + render.windowFrame.statusBarHeight.value.toInt()
        private val exitAnimationSpec: FiniteAnimationSpec<IntOffset> = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy,
        )

        override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionSlideVertically.enterTransition()")
                slideInVertically(enterAnimationSpec, initialOffsetY = { enterOffsetY })
            }

        override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionSlideVertically.exitTransition()")
                null
            }

        override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionSlideVertically.popEnterTransition()")
                null
            }

        override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
            get() = {
//                Log.v("[$label] ScreenTransitionSlideVertically.popExitTransition()")
                slideOutVertically(exitAnimationSpec, targetOffsetY = { exitOffsetY })
            }
    }

}