package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.declarations.IntoMapClassDeclaration

class IntoMapProcessor(
    private val environment: SymbolProcessorEnvironment,
    override val resolver: Resolver,
) : MultibindingAnnotationProcessor<IntoMapClassDeclaration> {

    override fun findAnnotations(): Sequence<IntoMapClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(IntoMap::class.qualifiedName.toString())
            .onEach { environment.logger.info("Found declaration $it") }
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                environment.logger.info("Found class declaration $declaration ${declaration.annotations.joinToString { it.shortName.asString() }}")
                val annotation = declaration.annotations
                    .firstOrNull { it.shortName.asString() == IntoMap::class.simpleName }
                    ?: return@mapNotNull null
                val mapName = annotation.arguments
                    .firstOrNull { it.name?.asString() == "mapName" }
                    ?.value?.toString()
                    ?: return@mapNotNull null
                val key = annotation.arguments
                    .firstOrNull { it.name?.asString() == "key" }
                    ?.value?.toString()?.toIntOrNull()
                    ?: return@mapNotNull null
                IntoMapClassDeclaration(
                    mapName,
                    key,
                    declaration.simpleName.asString(),
                    declaration.packageName.asString(),
                    declaration.primaryConstructor?.parameters?.mapNotNull { it.type.resolve().declaration.qualifiedName?.asString() } ?: emptyList(),
                    declaration.containingFile,
                )
            }
    }

    override fun processDeclarations(declarations: Iterable<IntoMapClassDeclaration>, fileSpec: FileSpec.Builder) {
        declarations.toList()
            .groupBy { it.mapName }
            .forEach { (map, declarations) ->
                environment.logger.info("Writing map $map")
                val items = declarations
                    .joinToString { declaration ->
                        "${declaration.key} to ${declaration.packageName}.${declaration.className}(${declaration.dependencyNames.joinToString()})"
                    }
                val funName = map
                    .filter { it.isLetterOrDigit() }
                    .plus("s")
                fileSpec.addFunction(
                    FunSpec.builder("get$funName")
                        .addParameters(declarations.flatMap { it.dependenciesAsParameters })
                        .addStatement("return mapOf(${items})")
                        .build()
                )
            }
    }
}
