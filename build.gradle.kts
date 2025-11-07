plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "oop.avengers.avengersgroup"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("oop.avengers.avengersgroup.AutoTrackApplication")
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")
    implementation("org.mongodb:bson:5.2.0")
    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


