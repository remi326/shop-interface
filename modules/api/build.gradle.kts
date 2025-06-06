plugins {
    id("hq.shared")
    id("hq.publish")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(framework.core)
}