package com.github.trueddd.data.model.save

import java.io.File

class GameSaveFileStructure(
    val actionsFile: File,
    val configFile: File,
) {

    constructor(parentFolder: File) : this(
        actionsFile = File(parentFolder, "actions.json"),
        configFile = File(parentFolder, "config.json"),
    )

    fun createFiles() {
        actionsFile.parentFile.mkdirs()
        actionsFile.createNewFile()
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
    }
}
