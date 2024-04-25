package com.trueddd.github.processors

import com.google.devtools.ksp.processing.Resolver
import com.squareup.kotlinpoet.FileSpec
import com.trueddd.github.declarations.ClassDeclaration

interface MultibindingAnnotationProcessor<D : ClassDeclaration> {

    val resolver: Resolver

    fun findAnnotations(): Sequence<D>

    fun processDeclarations(declarations: Iterable<D>, fileSpec: FileSpec.Builder)
}
