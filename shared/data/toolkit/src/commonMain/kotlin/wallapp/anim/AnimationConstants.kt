package wallapp.anim

/**
 * Change this to allow for faster flicks overall
 */
const val MAX_FLICK_VELOCITY = 20.0

/**
 * Changes the overall speed of animation regardless of the flick intensity
 */
const val TRANSLATION_ANIMATION_SCALER = 10 // Increase for faster translate animation
const val ROTATION_ANIMATION_SCALER = 17 // Increase for faster rotate animation

/**
 * Changes the duration of the flick animations, increasing will increase the duration
 */
const val ANIMATION_DURATION_VELOCITY_SCALER = 120
const val ROTATION_DURATION_VELOCITY_SCALER = 350

/**
 * Changes the velocity of the flick that results from changing pages on the home screen.
 *
 * Increase value to increase the velocity.
 * Directly proportional.
 */
const val HOME_OFFSET_FLICK_VELOCITY_SCALER = 1000

/**
 * Changes the velocity of the flick that results from swiping/flinging on the home page,
 * regardless of whether the homepage changes.
 *
 * Increase value to decrease the velocity.
 * Inversely proportional.
 */
const val FLING_VELOCITY_SCALER = 800

/**
 * Changes the velocity of the flick that results from onZoom() values changing on the home page,
 * for example when opening notification tray
 *
 * Increase value to decrease the velocity.
 * Inversely proportional.
 */
const val HOME_ZOOM_VELOCITY_SCALER = 1

/**
 * Changes the velocity of the flick that results from visibility change of home wallpaper. Every
 * time it becomes visible, a flick is registered.
 *
 * Increase value to increase the velocity.
 * Directly proportional.
 */
const val VISIBILITY_CHANGE_FLICK_VELOCITY_SCALER = 5f

/**
 * Changes the velocity of the flick that results from sudden azimuth change of device,
 * ie. any sudden device movement
 *
 * Increase value to increase the velocity. Both values in pair should be changed proportional to each other.
 * Directly proportional.
 */
val AZIMUTH_FLICK_VELOCITY_SCALER = Pair(8.5, 6.5)

/**
 * The maximum threshold for the number of multi-flicks before the speed resets.
 */
const val MULTI_FLICK_MAX_COUNT = 8

const val MULTI_FLICK_RESET_DELAY = 1400L

/**
 * These values influence the speed and duration of consecutive flicks after the first one.
 */
const val MULTI_FLICK_DURATION_MULTIPLIER = 1.05f
const val MULTI_FLICK_START_VALUE_DIVIDER = 1.2f

/**
 * Influence the amount of rotation per change in system zoom values.
 */
const val ZOOM_DELTA_SCALER = 20000

/**
 * Changes the value above which we throttle the system zoom change in a single step.
 */
const val ZOOM_CHANGE_OVER_TIME_THRESHOLD = 1

/**
 * Increase this value to decrease the amount of rotation resulting from system zoom change
 * when the zoom's change over time is above the [ZOOM_CHANGE_OVER_TIME_THRESHOLD]
 */
const val ZOOM_DELTA_THROTTLING_SCALER = .8f

/**
 * Directly proportional to the velocity of fling resulting from system zoom changes.
 * Increase it to increase the speed of the above mentioned fling/flick.
 */
const val ZOOM_FLING_VELOCITY_SCALER = 2000

const val SINGLE_TAP_FLICK_VELOCITY = 7.5f

const val DEFAULT_ZOOMED_IN_SCALE = .08f