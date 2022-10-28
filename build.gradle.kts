import net.ltgt.gradle.errorprone.errorprone

plugins {
    application
    `jvm-test-suite`
    idea
    jacoco
    id("com.diffplug.spotless") version "6.7.2"
    id("net.ltgt.errorprone") version "2.0.2"
}

val appModuleName = "org.${project.name}"
val appMainClassName = "org.${project.name}.cli.Main"
application {
    mainModule.set(appModuleName)
    mainClass.set(appMainClassName)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

spotless {
    java {
        palantirJavaFormat()
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    // Enable reproducible builds
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.withType<JavaCompile> {
    options.errorprone.disable("InvalidParam")
}

repositories {
    mavenCentral()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                compileOnly(libs.lombok)
                annotationProcessor(libs.lombok)
                implementation(libs.assertj)
            }
        }
    }
}

dependencies {
    configurations["errorprone"](libs.errorprone)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
