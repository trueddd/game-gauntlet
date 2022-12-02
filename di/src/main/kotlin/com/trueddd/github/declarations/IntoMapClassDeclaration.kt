package com.trueddd.github.declarations

import com.google.devtools.ksp.symbol.KSFile

class IntoMapClassDeclaration(
    val mapName: String,
    val key: Int,
    val className: String,
    val packageName: String,
    override val dependencies: List<String>,
    val containingFile: KSFile?,
) : ClassDeclaration
