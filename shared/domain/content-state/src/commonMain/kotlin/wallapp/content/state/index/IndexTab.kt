package wallapp.content.state.index

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class IndexTab {
    Account,

    Explore,

    Home,

//    Categories(
//    "Collections",
//        selectedIcon = Image.from(Icons.Category, contentDescription = null),
//        unselectedIcon = Image.from(Icons.CategoryUnselected, contentDescription = null),
//    ),
//
//    Collectors(
//        "Collectors",
//        selectedIcon = Image.from(Icons.Collector, contentDescription = null),
//        unselectedIcon = Image.from(Icons.CollectorUnselected, contentDescription = null),
//    ),

    ;
}