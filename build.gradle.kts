@file:Suppress("DEPRECATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*
import org.gradle.api.internal.HasConvention
import org.gradle.kotlin.dsl.provider.gradleKotlinDslOf

plugins {
    idea
    kotlin("jvm") version "1.6.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("com.google.protobuf") version "0.8.17"
    application
}

val Project.protobuf: ProtobufConvention get() =
    this.convention.getPlugin(ProtobufConvention::class)

group = "me.ondrejoa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


javafx {
    version = "11"
    modules("javafx.controls", "javafx.fxml")
}


dependencies {
    implementation("com.google.protobuf:protobuf-java:3.19.0-rc-1")
    implementation("com.google.protobuf:protobuf-kotlin:3.19.0-rc-1")
    implementation("org.openjfx:javafx:11")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] =  "MainKt"
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.0.0"
    }
}



application {
    mainClass.set("MainKt")
}
