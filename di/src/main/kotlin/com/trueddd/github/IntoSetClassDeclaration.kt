package com.trueddd.github

import com.google.devtools.ksp.symbol.KSAnnotation

data class IntoSetClassDeclaration(
    val setType: KSAnnotation,
    val className: String,
    val packageName: String,
    val dependencies: List<String>,
)
