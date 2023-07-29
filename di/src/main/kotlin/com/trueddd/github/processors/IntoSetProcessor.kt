package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.trueddd.github.declarations.IntoSetClassDeclaration
import com.trueddd.github.annotations.IntoSet
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

class IntoSetProcessor(
    private val environment: SymbolProcessorEnvironment,
    override val resolver: Resolver,
) : MultibindingAnnotationProcessor<IntoSetClassDeclaration> {

    override fun findAnnotations(): Sequence<IntoSetClassDeclaration> {
        return resolver.getSymbolsWithAnnotation(IntoSet::class.qualifiedName.toString())
            .onEach { environment.logger.info("Found declaration $it") }
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                environment.logger.info("Found class declaration $declaration ${declaration.annotations.joinToString { it.shortName.asString() }}")
                val setName = declaration.annotations
                    .firstOrNull { it.shortName.asString() == IntoSet::class.simpleName }
                    ?.arguments
                    ?.firstOrNull { it.name?.asString() == "setName" }
                    ?.value?.toString()
                    ?.also { environment.logger.info("setName: $it") }
                    ?: return@mapNotNull null
                IntoSetClassDeclaration(
                    setName,
                    declaration.qualifiedName?.asString() ?: declaration.simpleName.asString(),
                    if (declaration.qualifiedName == null) declaration.packageName.asString() else "",
                    declaration.primaryConstructor?.parameters?.mapNotNull { it.type.resolve().declaration.qualifiedName?.asString() } ?: emptyList(),
                    declaration.containingFile,
                )
            }
    }

    override fun processDeclarations(declarations: Iterable<IntoSetClassDeclaration>, fileSpec: FileSpec.Builder) {
        declarations.toList()
            .groupBy { it.setName }
            .forEach { (type, declarations) ->
                environment.logger.info("type: $type")
                val items = declarations.joinToString { it.callConstructor }
                fileSpec.addFunction(
                    FunSpec.builder("get${type}Set")
                        .addParameters(declarations.flatMap { it.dependenciesAsParameters })
                        .returns(SET.parameterizedBy(STAR))
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
