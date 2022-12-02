package com.trueddd.github.declarations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import java.util.*

interface ClassDeclaration {

    val dependencies: List<String>

    private fun String.decapitalize(): String {
        return replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }

    val dependencyNames: List<String>
        get() = dependencies
            .map { it.substringAfterLast(".") }
            .map { it.decapitalize() }

    val dependenciesAsParameters: List<ParameterSpec>
        get() {
            return dependencies.map {
                val name = it.substringAfterLast(".")
                val packageName = it.substringBeforeLast(".")
                ParameterSpec(name.decapitalize(), ClassName(packageName, name))
            }
        }
}
