import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("hq.shared")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(framework.core)
    //compileOnly(libs.nexomc) //Nexo
    compileOnly(fileTree("C:/Project_x/lib")) //Nexo
    compileOnly(framework.command)
    compileOnly(framework.database)
    compileOnly(framework.inventory)
    compileOnly(framework.nms)
    compileOnly(libs.fawe)

    compileOnly(project(":modules:api"))
}
