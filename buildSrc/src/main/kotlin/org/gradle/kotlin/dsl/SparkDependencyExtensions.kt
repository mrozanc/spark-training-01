package org.gradle.kotlin.dsl

import org.gradle.api.internal.provider.DefaultProvider

private val SPARK_GROUP = "org.apache.spark"

fun DependencyHandlerScope.spark(sparkLib: String) = "$SPARK_GROUP:spark-$sparkLib"
