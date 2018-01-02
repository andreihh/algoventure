-keepattributes RuntimeVisibleAnnotations

-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.jvm.internal.**

-keepnames class com.fasterxml.** { *; }
-dontwarn com.fasterxml.**

-keep class com.andreihh.** { *; }
-keepnames class com.andreihh.** { *; }
