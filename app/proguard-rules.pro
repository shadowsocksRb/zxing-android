-allowaccessmodification
-repackageclasses

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-assumenosideeffects class android.util.Log {
    public static int d(...);
}
