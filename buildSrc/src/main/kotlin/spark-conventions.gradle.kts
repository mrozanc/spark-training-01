import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import de.undercouch.gradle.tasks.download.Download
import fr.rozanc.gradle.spark.SparkExtension

plugins {
    id("scala")
    id("de.undercouch.download")
    id("com.gradleup.shadow")
}

private val sparkExtension = extensions.create("spark", SparkExtension::class)

configurations.all {
    resolutionStrategy.dependencySubstitution.all {
        requested.let {
            if (it is ModuleComponentSelector && it.group == SparkExtension.SPARK_GROUP) {
                val baseModule = it.module.substringBeforeLast("_")
                useTarget("${SparkExtension.SPARK_GROUP}:${baseModule}_${sparkExtension.scalaVersion.get()}:${sparkExtension.sparkVersion.get()}")
            }
        }
    }
}

val sparkDistUrl = sparkExtension.sparkVersion.map { sparkVersion ->
    uri("https://archive.apache.org/dist/spark/spark-$sparkVersion/spark-$sparkVersion-bin-hadoop3.tgz")
}

tasks {
    val downloadSparkDist = register<Download>("downloadSparkDist") {
        group = "spark"
        src(sparkDistUrl)
        dest(sparkDistUrl.flatMap {
            layout.buildDirectory.file(
                "archive/spark-dist/${
                    it.path.substringAfterLast('/')
                }"
            )
        })
        //onlyIfModified(true)
        overwrite(false)
    }
    register<Sync>("unpackSparkDist") {
        group = "spark"
        from(tarTree(downloadSparkDist.map { it.dest })) {
            include("*/**")
            includeEmptyDirs = false
            eachFile {
                relativePath = RelativePath(!isDirectory(), *relativePath.segments.drop(1).toTypedArray())
            }
        }
        into(layout.buildDirectory.dir("dist/spark"))
    }
    named<ShadowJar>("shadowJar") {
        isZip64 = true
        dependencies {
            val nameAndVersionRegex = Regex("^(.+?)-(\\d.+?)(?:-([a-z][\\w-]*))?\\.jar$")
            files(tasks.named<Sync>("unpackSparkDist").map { "${it.destinationDir}/jars" }).files.asSequence()
                .flatMap { it.listFiles()?.asSequence() ?: emptySequence() }
                .filter { it.isFile && it.name.endsWith(".jar") }
                .forEach { jarFile ->
                    val matchResult = nameAndVersionRegex.matchEntire(jarFile.name)
                    val artifactId = matchResult?.groupValues?.get(1)
                    val artifactVersion = matchResult?.groupValues?.get(2)
                    val classifier = matchResult?.groupValues?.get(3)
                    exclude(dependency(":$artifactId:$artifactVersion"))
                }
        }
    }
    named("assemble") { dependsOn("shadowJar") }
}
