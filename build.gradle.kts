import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

group = "com.runescape"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    jacoco
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

ktlint {
    version.set("0.37.2")
    outputToConsole.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.destination = file("$buildDir/jacocoHtml")
    }
    dependsOn(tasks.test)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
