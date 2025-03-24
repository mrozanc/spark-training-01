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
    register<Exec>("dockerBuild") {
        group = "docker"
        dependsOn("jar")
        workingDir(project.projectDir)
        commandLine(
            "docker",
            "build",
            "--build-arg",
            "app_version=${project.version}",
            "-t",
            "wtskayansparkall:${project.version}",
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
            "wtskayansparkall:${project.version}"
        )
    }
}
