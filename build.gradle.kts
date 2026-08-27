import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "me.znotchill"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.znotchill.me/repository/maven-releases/")
}

kotlin {
    linuxX64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            executable {
                entryPoint = "me.znotchill.kelp.main"
                linkerOpts("-L/usr/lib", "-Wl,--allow-shlib-undefined")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("me.znotchill.kiwi:core:1.0.0")

            compileOnly("io.github.smyrgeorge:sqlx4k:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-postgres:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-mysql:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-sqlite:1.13.0")
        }
    }
}