plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

configurations.compileClasspath {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.compileJava {
    options.release.set(21)
}

group = "me.bill.fpp"
version = "1.6.6.10"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.faststats.dev/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // paperweight.paperDevBundle("26.1.2.build.+") // todo: 26.1.2 support
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    implementation("dev.faststats.metrics:bukkit:0.22.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("com.mysql:mysql-connector-j:8.3.0") {
        exclude("com.google.protobuf", "protobuf-java")
    }

    compileOnly("net.luckperms:api:5.5")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12") {
        exclude("com.google.code.gson", "gson")
        exclude("com.google.guava", "guava")
        exclude("it.unimi.dsi", "fastutil")
    }
}

tasks.shadowJar {
    archiveBaseName.set("fake-player-plugin")
    relocate("org.bstats", "net.thenextlvl.portals.bstats")
}
