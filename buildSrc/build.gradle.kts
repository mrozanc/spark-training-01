import org.gradle.api.internal.artifacts.DefaultModuleIdentifier
import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint

plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(plugin(libs.plugins.shadow))
    implementation(plugin(libs.plugins.download))
    implementation(libs.kotlinGradlePlugin)
}

fun Provider<MinimalExternalModuleDependency>.withVersion(version: String) =
    this.map { DefaultMinimalDependency(it.module, DefaultMutableVersionConstraint(version)) }

fun DependencyHandlerScope.plugin(pluginDependency: Provider<PluginDependency>, version: String? = null) =
    pluginDependency.map { it.toModuleDependency(version) }

fun PluginDependency.toModuleDependency(v: String? = null) =
    DefaultMinimalDependency(DefaultModuleIdentifier.newId(pluginId, "$pluginId.gradle.plugin"),
        v?.let { DefaultMutableVersionConstraint(it) } ?: DefaultMutableVersionConstraint(version))
