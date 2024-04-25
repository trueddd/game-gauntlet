package com.trueddd.github.declarations

import com.google.devtools.ksp.symbol.KSFile

data class IntoSetClassDeclaration(
    val setName: String,
    override val className: String,
    override val packageName: String,
    override val dependencies: List<String>,
    val containingFile: KSFile?,
) : ClassDeclaration
