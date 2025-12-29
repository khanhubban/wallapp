Notes on iOS compile times

# Overview

This document is a collection of notes on iOS compile times for the app. Please update as 
new information is discovered.

ATM it seems like the best solution is "get a fast Apple Silicon Mac with lots of RAM".

# Relevant posts

* https://youtrack.jetbrains.com/issue/KT-42294/Improve-Kotlin-Native-compilation-time
* https://youtrack.jetbrains.com/issue/KT-70308/Build-ios-application-still-takes-long-time-in-kotlin-2.0.0
* https://youtrack.jetbrains.com/issue/KT-49385/Kotlin-Native-Parallelize-link-tasks-for-different-targets

# Noteworthy summary of points that impact compile times

From [here](https://youtrack.jetbrains.com/issue/KT-70308/Build-ios-application-still-takes-long-time-in-kotlin-2.0.0#focus=Comments-27-10252182.0-0)

> We are strongly encouraging you to try the combination of 2.0.10-RC kotlin version with kotlinx-coroutines version 1.8.1 or higher with turned on kotlin.incremental.native and check the incremental compilation times.
>
> As for the main topic - is it possible to share with us some details about your project? Like, the following:
> 
> 1. do you use Compose
> 2. how many exported libraries does you build script contain
> 3. which dependencies do you use
> 4. how many modules does your project contain
> 5. how do you integrate your kotlin into iOS project?
> 6. Do you have "explicit api mode" turned on for your exported projects?

#2 is particularly relevant. 