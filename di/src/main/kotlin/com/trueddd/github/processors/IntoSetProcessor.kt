package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.trueddd.github.annotations.ActionGenerator
import com.trueddd.github.declarations.IntoSetClassDeclaration
import com.trueddd.github.annotations.IntoSet
import com.trueddd.github.annotations.ItemFactory
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

class IntoSetProcessor(
    private val environment: SymbolProcessorEnvironment,
    override val resolver: Resolver,
) : MultibindingAnnotationProcessor<IntoSetClassDeclaration> {

    override fun findAnnotations(): Sequence<IntoSetClassDeclaration> {
        val availableAnnotationSimpleNames = listOf(
            IntoSet::class.simpleName,
            ActionGenerator::class.simpleName,
            ItemFactory::class.simpleName,
        )
        return resolver.getSymbolsWithAnnotation(IntoSet::class.qualifiedName.toString())
            .plus(resolver.getSymbolsWithAnnotation(ActionGenerator::class.qualifiedName.toString()))
            .plus(resolver.getSymbolsWithAnnotation(ItemFactory::class.qualifiedName.toString()))
            .onEach { environment.logger.info("Found declaration $it") }
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                environment.logger.info("Found class declaration $declaration ${declaration.annotations.joinToString { it.shortName.asString() }}")
                val setName = declaration.annotations
                    .firstOrNull { it.shortName.asString() in availableAnnotationSimpleNames }
                    ?.let { annotation ->
                        when (annotation.shortName.asString()) {
                            ActionGenerator::class.simpleName -> ActionGenerator.TAG
                            ItemFactory::class.simpleName -> ItemFactory.TAG
                            else -> annotation.arguments.firstOrNull { it.name?.asString() == "setName" }?.value?.toString()
                        }
                    }
                    ?.also { environment.logger.info("setName: $it") }
                    ?: return@mapNotNull null
                IntoSetClassDeclaration(
                    setName = setName,
                    className = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString(),
                    packageName = if (declaration.qualifiedName == null) declaration.packageName.asString() else "",
                    dependencies = declaration.primaryConstructor?.parameters?.mapNotNull {
                        it.type.resolve().declaration.qualifiedName?.asString()
                    } ?: emptyList(),
                    containingFile = declaration.containingFile,
                )
            }
    }

    override fun processDeclarations(declarations: Iterable<IntoSetClassDeclaration>, fileSpec: FileSpec.Builder) {
        declarations.toList()
            .groupBy { it.setName }
            .forEach { (type, declarations) ->
                environment.logger.info("type: $type")
                val items = declarations.joinToString { it.callConstructor }
                val returnType = when (type) {
                    ItemFactory.TAG -> ClassName(
                        "com.github.trueddd.data.items",
                        "WheelItem", "Factory"
                    )
                    ActionGenerator.TAG -> ClassName(
                        "com.github.trueddd.core.actions",
                        "Action", "Generator"
                    ).parameterizedBy(STAR)
                    else -> STAR
                }.let { SET.parameterizedBy(it) }
                fileSpec.addFunction(
                    FunSpec.builder("get${type}Set")
                        .addParameters(declarations.flatMap { it.dependenciesAsParameters }.distinct())
                        .returns(returnType)
                        .addStatement("return setOf(${items})")
                        .addAnnotation(Single::class)
                        .addAnnotation(
                            AnnotationSpec.builder(Named::class)
                                .addMember("value = %S", type)
                                .build()
                        )
                        .build()
                )
            }
    }
}
