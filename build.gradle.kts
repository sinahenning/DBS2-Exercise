import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "1.7.20"
    id("java")
    application

    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    idea
}

group = "de.hpi.dbs2"
version = "1.0-SNAPSHOT"

val groupIdentifier = project.property("groupIdentifier") as String
require(groupIdentifier.isNotBlank()) {
    "Please set your group identifier in the gradle.properties file"
}
println("Using groupIdentifier=$groupIdentifier")

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.github.ajalt.clikt:clikt:3.5.0")

    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.5.1")
}

application {
    applicationName = "dbs2exercises"
    mainClass.set("$group.exerciseframework.MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    withType<JavaExec> {
        enableAssertions = true
    }
    test {
        enableAssertions = true
        testLogging {
            showStandardStreams = true
        }
        useJUnitPlatform()
    }
    withType<KotlinJvmCompile> {
        kotlinOptions {
            apiVersion = "1.7"
            languageVersion = "1.7"
            freeCompilerArgs = listOf("-Xcontext-receivers")
        }
    }
    withType<Jar> {
        manifest {
            attributes(
                "Main-Class" to application.mainClass
            )
        }
    }
    withType<ShadowJar> {
        archiveFileName.set("dbs2exercises.jar")
    }
    listOf(
        "exercise0",
        "exercise1",
        "exercise2",
    ).forEach { exerciseDescriptor ->
        register<Zip>("pack${exerciseDescriptor.capitalized()}") {
            archiveFileName.set("group$groupIdentifier-$exerciseDescriptor.zip")
            from("src/main/kotlin/$exerciseDescriptor/") {
                into("kotlin")
            }
            from("src/main/java/$exerciseDescriptor/") {
                into("java")
            }
            destinationDirectory.set(File("."))
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
