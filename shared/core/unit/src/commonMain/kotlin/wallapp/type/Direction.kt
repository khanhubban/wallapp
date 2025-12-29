package wallapp.type

enum class Direction {
    NORTH, SOUTH, EAST, WEST, NONE
}

fun Direction.sameAxisAs(direction: Direction): Boolean =
    when (this) {
        Direction.NORTH, Direction.SOUTH -> direction == Direction.NORTH || direction == Direction.SOUTH
        Direction.EAST, Direction.WEST -> direction == Direction.WEST || direction == Direction.EAST
        Direction.NONE -> false
    }

fun Direction.isNorthSouth(): Boolean =
    this == Direction.NORTH || this == Direction.SOUTH

fun Direction.invertDirection(): Direction = when(this) {
    Direction.NORTH -> Direction.SOUTH
    Direction.SOUTH -> Direction.NORTH
    Direction.EAST -> Direction.WEST
    Direction.WEST -> Direction.EAST
    Direction.NONE -> Direction.NONE
}