package com.trueddd.github.processors

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.trueddd.github.IntoSetClassDeclaration
import com.trueddd.github.annotations.IntoSet
import kotlin.reflect.KClass

class MultibindingProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private fun Resolver.findAnnotations(kClass: KClass<*>): Sequence<IntoSetClassDeclaration> {
        return getSymbolsWithAnnotation(kClass.qualifiedName.toString())
            .onEach { environment.logger.info("Found declaration $it") }
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                environment.logger.info("Found class declaration $declaration ${declaration.annotations.joinToString { it.shortName.asString() }}")
                environment.logger.info("anno ${declaration.annotations.firstOrNull()?.annotationType}")
                val annotation = declaration.annotations
                    .firstOrNull { it.shortName.asString() == IntoSet::class.simpleName }
                    ?: return@mapNotNull null
                IntoSetClassDeclaration(
                    annotation,
                    declaration.simpleName.asString(),
                    declaration.packageName.asString(),
                    declaration.primaryConstructor?.parameters?.mapNotNull { it.type.resolve().declaration.qualifiedName?.asString() } ?: emptyList(),
                )
            }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        environment.logger.info("entered processor")
        val intoSetDeclarations = resolver.findAnnotations(IntoSet::class)
        if (!intoSetDeclarations.iterator().hasNext()) return emptyList()
        val intoSetClassNames = intoSetDeclarations
            .groupBy { it.setType }
        val generatedFile = FileSpec.builder("com.github.trueddd.di", "Multibindings")
            .apply {
                intoSetClassNames.forEach { (type, declaration) ->
                    val name = type.shortName.asString()
                    val count = declaration.size
                    addProperty(
                        PropertySpec.builder(
                            name,
                            ClassName(name.substringBeforeLast("."), name.substringAfterLast(".")),
                        )
                            .initializer("listOf(%S)", "$count")
                            .build()
                    )
                }
            }
            .build()
        val dependencies = Dependencies(false, *intoSetDeclarations.mapNotNull { it.setType.containingFile }.toList().toTypedArray())
        val outputFile = environment.codeGenerator.createNewFile(dependencies, generatedFile.packageName, generatedFile.name)
        environment.logger.info("FileSpec finished, writing")
        generatedFile.writeTo(outputFile.bufferedWriter())
        return emptyList()
    }
}
