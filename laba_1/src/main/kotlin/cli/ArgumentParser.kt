package org.example.cli

class ArgumentParser {
    fun parse(args: Array<String>): Command {
        if (args.size != 2) {
            return Command.InvalidCommand
        }

        if (args[0] != "list") {
            return Command.InvalidCommand
        }

        if (!args[1].startsWith("--tasks-file=")) {
            return Command.InvalidCommand
        }

        val filePath = args[1].removePrefix("--tasks-file=")
        if (filePath.isBlank()) {
            return Command.InvalidCommand
        }

        return Command.ListCommand(filePath)
    }
}