import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.openjfx.javafxplugin") version "0.0.10"
    application
}

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
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}