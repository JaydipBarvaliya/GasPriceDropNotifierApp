// File: <root>/build.gradle.kts

plugins {
    // Typically left empty at the root if you're using the modern plugins DSL
    // in your module-level build.gradle.kts
}

buildscript {
    // Generally no dependencies needed here if you're declaring
    // plugin versions in the module-level plugins { } block
    dependencies {
        // e.g., if you needed a legacy plugin classpath:
        // classpath("com.android.tools.build:gradle:<version>")
    }
}

allprojects {
    // Provide common repositories for all subprojects if needed
    repositories {
    }
}
