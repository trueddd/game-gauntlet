package com.trueddd.github.declarations

import com.google.devtools.ksp.symbol.KSFile

class IntoMapClassDeclaration(
    val mapName: String,
    val key: Int,
    override val className: String,
    override val packageName: String,
    override val dependencies: List<String>,
    val containingFile: KSFile?,
) : ClassDeclaration
