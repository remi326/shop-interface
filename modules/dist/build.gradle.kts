plugins {
    id("hq.shared")
    id("hq.shadow")
    id("kr.hqservice.resource-generator.bukkit")
}

bukkitResourceGenerator {
    main = "kr.hqservice.project_x.practice.character_stat"
    name = "${extra["projectName"]}"
    apiVersion = "1.13"
    depend = listOf("HQFramework")
    libraries = excludedRuntimeDependencies()
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(framework.core)
    runtimeOnly(project(":modules:core"))
    runtimeOnly(project(":modules:api"))
}