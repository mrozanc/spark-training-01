package fr.rozanc.gradle.spark

import org.gradle.api.Project
import org.gradle.api.plugins.scala.ScalaPluginExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property

open class SparkExtension(project: Project) {
    companion object {
        val SPARK_GROUP = "org.apache.spark"
    }

    val sparkVersion = project.objects.property<String>().convention("3.5.5")
    val scalaVersion = project.objects.property<String>().value(
        project.extensions.getByType<ScalaPluginExtension>().scalaVersion
            .map { it.split('.', limit = 3).subList(0, 2).joinToString(".") })
        .apply { disallowChanges() }
}
