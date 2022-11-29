package com.trueddd.github.processors

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
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
        val fileSpec = FileSpec.builder("com.github.trueddd.di", "Multibindings")
        intoSetDeclarations
            .toList()
            .groupBy { it.setType.shortName.asString() }
            .forEach { (type, declarations) ->
                environment.logger.info("type: $type")
                val items = declarations
                    .joinToString { declaration ->
                        "${declaration.packageName}.${declaration.className}(${declaration.dependencyNames.joinToString()})"
                    }
                val funName = declarations.first().setType.arguments
                    .firstOrNull { it.name?.asString() == "superType" }
                    ?.value?.toString()
                    ?.filter { it.isLetterOrDigit() }
                    ?.plus("s")
                    ?: type
                fileSpec.addFunction(
                    FunSpec.builder("get$funName")
                        .addParameters(declarations.flatMap { it.dependenciesAsParameters })
                        .addStatement("return setOf(${items})")
                        .build()
                )
            }
        val generatedFile = fileSpec.build()
        val dependencies = Dependencies(false, *intoSetDeclarations.mapNotNull { it.setType.containingFile }.toList().toTypedArray())
        val outputFile = environment.codeGenerator.createNewFile(dependencies, generatedFile.packageName, generatedFile.name)
        environment.logger.info("FileSpec finished, writing")
        outputFile.write(generatedFile.toString().encodeToByteArray())
        return emptyList()
    }
}
