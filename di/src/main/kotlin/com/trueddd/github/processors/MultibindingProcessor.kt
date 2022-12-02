package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.FileSpec

class MultibindingProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        environment.logger.info("entered processor")
        val intoSetProcessor = IntoSetProcessor(environment, resolver)
        val intoSetDeclarations = intoSetProcessor.findAnnotations().toList()

        val intoMapProcessor = IntoMapProcessor(environment, resolver)
        val intoMapDeclarations = intoMapProcessor.findAnnotations().toList()

        if (intoSetDeclarations.isEmpty() && intoMapDeclarations.isEmpty()) return emptyList()

        val fileSpec = FileSpec.builder("com.github.trueddd.di", "Multibindings")
        intoSetProcessor.processDeclarations(intoSetDeclarations, fileSpec)
        intoMapProcessor.processDeclarations(intoMapDeclarations, fileSpec)

        val generatedFile = fileSpec.build()
        val allFiles = intoSetDeclarations.mapNotNull { it.containingFile }.toList() + intoMapDeclarations.mapNotNull { it.containingFile }.toList()
        val dependencies = Dependencies(false, *allFiles.toTypedArray())
        val outputFile = environment.codeGenerator.createNewFile(dependencies, generatedFile.packageName, generatedFile.name)
        environment.logger.info("FileSpec finished, writing")
        outputFile.write(generatedFile.toString().encodeToByteArray())
        return emptyList()
    }
}
