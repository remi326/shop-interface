plugins {
    id("hq.shared")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(framework.core)
    compileOnly(project(":modules:api"))
}