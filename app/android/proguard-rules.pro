# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Remove all Log items. This also removes strings passed to Log methods. #
-assumenosideeffects class wallapp.log.Log { *; }
-assumenosideeffects class wallapp.log.Logger { *; }

-keep class wallapp.wallpapers.network.** { *; }

#-keep class wallapp.content.model.WallpaperColors { *; }

-keep class wallapp.content.model.WallpaperLayerLayoutParams { *; }

-keep class wallapp.types.* { *; }

# The classes that return data from the license server
-keep class t.a.b0.** { *; }

-keep class wallapp.image.glide.** { *; }

-keep class com.google.gson.** { *; }

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# AGP 8.0 enables R8 full mode by default. When enabled, firebase has issues. Keeping this class is
# the work around. See https://github.com/firebase/firebase-android-sdk/issues/2124#issuecomment-920922929
-keep public class com.google.firebase.** {*;}
-keep class com.google.android.gms.internal.** {*;}
-keepclasseswithmembers class com.google.firebase.FirebaseException

#Okhttp warnings
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Billing-related warning
-dontwarn org.slf4j.impl.StaticLoggerBinder

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }

-keep class wallapp.userprofile.UserProfileFlag { *; }
-keep class wallapp.userprofile.UserProfileLocal { *; }
-keep class wallapp.userprofile.UserProfileRemote { *; }

# Keep Play-Services nullness annotations referenced by Play Core KTX
-keep class com.google.android.gms.common.annotation.NoNullnessRewrite { *; }
-keep class com.google.android.gms.common.annotation.** { *; }
# Work around Release linking issue: #2480, https://issuetracker.google.com/issues/374691245
-dontwarn com.google.android.gms.common.annotation.**

#-printusage ../../export/android-proguard-usage.txt
