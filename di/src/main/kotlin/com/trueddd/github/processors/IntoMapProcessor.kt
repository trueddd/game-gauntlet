package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.trueddd.github.annotations.ActionHandler
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.declarations.IntoMapClassDeclaration
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

class IntoMapProcessor(
    private val environment: SymbolProcessorEnvironment,
    override val resolver: Resolver,
) : MultibindingAnnotationProcessor<IntoMapClassDeclaration> {

    override fun findAnnotations(): Sequence<IntoMapClassDeclaration> {
        val availableAnnotationSimpleNames = listOf(
            IntoMap::class.simpleName,
            ActionHandler::class.simpleName,
        )
        return resolver.getSymbolsWithAnnotation(IntoMap::class.qualifiedName.toString())
            .plus(resolver.getSymbolsWithAnnotation(ActionHandler::class.qualifiedName.toString()))
            .onEach { environment.logger.info("Found declaration $it") }
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                environment.logger.info("Found class declaration $declaration ${declaration.annotations.joinToString { it.shortName.asString() }}")
                val annotation = declaration.annotations
                    .firstOrNull { it.shortName.asString() in availableAnnotationSimpleNames }
                    ?: return@mapNotNull null
                val mapName = when (annotation.shortName.asString()) {
                    ActionHandler::class.simpleName -> ActionHandler.TAG
                    else -> annotation.arguments.firstOrNull { it.name?.asString() == "mapName" }?.value?.toString()
                } ?: return@mapNotNull null
                val key = annotation.arguments
                    .firstOrNull { it.name?.asString() == "key" }
                    ?.value?.toString()?.toIntOrNull()
                    ?: return@mapNotNull null
                IntoMapClassDeclaration(
                    mapName = mapName,
                    key = key,
                    className = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString(),
                    packageName = if (declaration.qualifiedName == null) declaration.packageName.asString() else "",
                    dependencies = declaration.primaryConstructor?.parameters?.mapNotNull {
                        it.type.resolve().declaration.qualifiedName?.asString()
                    } ?: emptyList(),
                    containingFile = declaration.containingFile,
                )
            }
    }

    override fun processDeclarations(declarations: Iterable<IntoMapClassDeclaration>, fileSpec: FileSpec.Builder) {
        declarations.toList()
            .groupBy { it.mapName }
            .forEach { (map, declarations) ->
                environment.logger.info("Writing map $map")
                val items = declarations.joinToString { "${it.key} to ${it.callConstructor}" }
                val returnType = when (map) {
                    ActionHandler.TAG -> listOf(
                        INT, ClassName(
                            "com.github.trueddd.core.actions",
                            "Action", "Handler"
                        ).parameterizedBy(STAR)
                    )
                    else -> listOf(INT, STAR)
                }.let { MAP.parameterizedBy(it) }
                fileSpec.addFunction(
                    FunSpec.builder("get${map}Map")
                        .addParameters(declarations.flatMap { it.dependenciesAsParameters }.distinct())
                        .returns(returnType)
                        .addStatement("return mapOf(${items})")
                        .addAnnotation(Single::class)
                        .addAnnotation(
                            AnnotationSpec.builder(Named::class)
                                .addMember("value = %S", map)
                                .build()
                        )
                        .build()
                )
            }
    }
}
