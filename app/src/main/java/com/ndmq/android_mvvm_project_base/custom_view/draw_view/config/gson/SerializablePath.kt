package com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson

import android.graphics.Path


class SerializablePath : Path {


    constructor() : super()

    constructor(src: SerializablePath) : super(src) {
        commands.addAll(src.commands)
    }

    private val commands = mutableListOf<PathCommand>()

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        commands.add(PathCommand("moveTo", x, y))
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        super.quadTo(x1, y1, x2, y2)
        commands.add(PathCommand("quadTo", x1, y1, x2, y2))
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        commands.add(PathCommand("lineTo", x, y))
    }

    override fun reset() {
        super.reset()
        commands.clear()
    }

    fun getCommands(): List<PathCommand> {
        return commands
    }


    class PathCommand(var type: String, vararg var params: Float)
}