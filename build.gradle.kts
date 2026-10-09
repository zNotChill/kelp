import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinxSerialization)
}

group = "me.znotchill"
version = "1.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.znotchill.me/repository/maven-releases/")
}

kotlin {
    jvm()

    macosArm64()
    linuxArm64()
    linuxX64()
    mingwX64()

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
            api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            implementation("me.znotchill.kiwi:core:1.0.1")

            compileOnly("io.github.smyrgeorge:sqlx4k:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-postgres:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-mysql:1.13.0")
            compileOnly("io.github.smyrgeorge:sqlx4k-sqlite:1.13.0")
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<MavenPublishBaseExtension> {
        coordinates(
            groupId = "me.znotchill.kelp",
            artifactId = project.name,
            version = rootProject.version.toString()
        )
    }
}
pluginManager.withPlugin("maven-publish") {
    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "znotchill"
                url = uri("https://repo.znotchill.me/releases")

                credentials {
                    username = rootProject.findProperty("zRepoUsername") as String?
                        ?: System.getenv("MAVEN_USER")
                    password = rootProject.findProperty("zRepoPassword") as String?
                        ?: System.getenv("MAVEN_PASS")
                }
            }
        }
    }
}