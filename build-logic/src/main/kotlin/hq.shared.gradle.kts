plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = extra["projectGroup"]!!.toString()
version = extra["projectVersion"]!!.toString()

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}