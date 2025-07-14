plugins {
    java
    id("com.github.johnrengelman.shadow")
}

tasks.shadowJar {
    archiveBaseName.set(project.rootProject.name)
    archiveVersion.set("")
    archiveClassifier.set("")
   // destinationDirectory.set(file(rootProject.projectDir.path + "/build_outputs"))
    destinationDirectory.set(file("C:/Users/Administrator/Desktop/practice_1.21.1/plugins"))
}

configurations.runtimeClasspath.get().apply {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}