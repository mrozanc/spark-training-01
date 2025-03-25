import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Paths

plugins {
    `spark-conventions`
}

scala {
    scalaVersion = "2.12.18"
}

dependencies {
    implementation("com.typesafe:config:1.4.3")
    implementation(spark("sql"))
    implementation(spark("catalyst"))
    implementation(spark("core"))
    implementation("org.scala-lang:scala-library")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()
    }
    val shadowJar = named<ShadowJar>("shadowJar")
    register<Exec>("dockerBuild") {
        group = "docker"
        dependsOn(shadowJar)
        workingDir(project.projectDir)
        commandLine(
            "docker",
            "build",
            "--build-arg",
            "jar_path=${
                project.projectDir.toPath()
                    .relativize(Paths.get(shadowJar.get().outputs.files.singleFile.path))
                    .toString().replace('\\', '/')
            }",
            "-t",
            "${project.name}:${project.version}",
            project.layout.projectDirectory.toString()
        )
    }
    register<Exec>("dockerRun") {
        group = "docker"
        dependsOn("dockerBuild")
        workingDir(project.projectDir)
        commandLine(
            "docker",
            "run",
            "-v",
            "${project.layout.projectDirectory.asFile.absolutePath}/src/data:/prd/project/datalake",
            "--rm",
            "--name",
            project.name,
            "${project.name}:${project.version}"
        )
    }
}
