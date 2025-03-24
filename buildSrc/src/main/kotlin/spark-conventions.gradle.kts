import fr.rozanc.gradle.spark.SparkExtension

plugins {
    id("scala")
}

//extensions.getByType<JavaPluginExtension>().apply {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(17))
//    }
//}


private val sparkGroup = "org.apache.spark"
private val sparkExtension = extensions.create("spark", SparkExtension::class)

configurations.all {
    resolutionStrategy.dependencySubstitution.all {
        requested.let {
            if (it is ModuleComponentSelector && it.group == sparkGroup) {
                val baseModule = it.module.substringBeforeLast("_")
                useTarget("$sparkGroup:${baseModule}_${sparkExtension.scalaVersion.get()}:${sparkExtension.sparkVersion.get()}")
            }
        }
    }
}



