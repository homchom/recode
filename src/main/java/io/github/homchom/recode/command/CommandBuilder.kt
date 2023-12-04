package io.github.homchom.recode.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicNCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.concurrent.CompletableFuture

typealias RecodeCommandDispatcher = CommandDispatcher<FabricClientCommandSource>

typealias CommandBuilderScope = CommandBuilder<*>.() -> Unit
typealias CommandArgumentBuilder = ArgumentBuilder<FabricClientCommandSource, *>

inline fun commandRegistration(crossinline callback: RecodeCommandDispatcher.() -> Unit) {
    // TODO: what is CommandBuildContext for?
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ -> dispatcher.callback() }
}

inline fun RecodeCommandDispatcher.register(
    name: String,
    crossinline builder: CommandBuilderScope
): LiteralCommandNode<FabricClientCommandSource> {
    val literal = ClientCommandManager.literal(name)
    CommandBuilder(literal).builder()
    return register(literal)
}

@Suppress("unused")
@JvmInline
value class CommandBuilder<T : CommandArgumentBuilder>(val node: T) {
    // arguments
    fun literal(name: String): LiteralArgumentBuilder<FabricClientCommandSource> =
        ClientCommandManager.literal(name)

    operator fun <T> ArgumentType<T>.invoke(name: String): RequiredArgumentBuilder<FabricClientCommandSource, T> =
        ClientCommandManager.argument(name, this)

    inline fun then(argument: CommandArgumentBuilder, builder: CommandBuilderScope) {
        CommandBuilder(argument).builder()
        node.then(argument)
    }

    fun then(arguments: Array<out CommandArgumentBuilder>, builder: CommandBuilderScope) {
        fun recurse(node: CommandArgumentBuilder, queue: ArrayDeque<CommandArgumentBuilder>) {
            queue.singleOrNull()?.let { last ->
                CommandBuilder(last).builder()
                node.then(last)
                return
            }
            val next = queue.removeFirst()
            recurse(next, queue)
            node.then(next)
        }

        val queue = ArrayDeque<CommandArgumentBuilder>(arguments.size)
        queue.addAll(arguments)
        recurse(node, queue)
    }

    // requirements
    fun requires(predicate: (FabricClientCommandSource) -> Boolean) {
        node.requires(predicate)
    }

    // execution
    inline fun executes(crossinline command: (CommandContext<FabricClientCommandSource>) -> Unit) {
        node.executes { context ->
            command(context)
            1 // nobody seems to know why brigadier asks for an int here
        }
    }

    // exceptions
    fun exception(message: Message) = SimpleCommandExceptionType(message)

    fun exception(dynamicMessage: (Array<out Any>) -> Message) = DynamicNCommandExceptionType(dynamicMessage)

    inline val builtInExceptions: BuiltInExceptionProvider get() =
        CommandSyntaxException.BUILT_IN_EXCEPTIONS

    // suggestions
    fun interface SuggestionProvider :
        com.mojang.brigadier.suggestion.SuggestionProvider<FabricClientCommandSource>
    {
        fun SuggestionsBuilder.provideSuggestions(context: CommandContext<FabricClientCommandSource>)

        override fun getSuggestions(
            context: CommandContext<FabricClientCommandSource>,
            builder: SuggestionsBuilder
        ): CompletableFuture<Suggestions> {
            builder.provideSuggestions(context)
            return builder.buildFuture()
        }
    }

    // redirects

    fun redirect(target: CommandNode<FabricClientCommandSource>) {
        node.redirect(target)
    }

    // shorthands for common argument types
    inline val bool: BoolArgumentType get() = BoolArgumentType.bool()
    inline val double: DoubleArgumentType get() = DoubleArgumentType.doubleArg()
    inline val float: FloatArgumentType get() = FloatArgumentType.floatArg()
    inline val integer: IntegerArgumentType get() = IntegerArgumentType.integer()
    inline val long: LongArgumentType get() = LongArgumentType.longArg()
    inline val word: StringArgumentType get() = StringArgumentType.word()
    inline val string: StringArgumentType get() = StringArgumentType.string()
    inline val greedyString: StringArgumentType get() = StringArgumentType.greedyString()

    fun double(min: Double): DoubleArgumentType = DoubleArgumentType.doubleArg(min)
    fun double(min: Double, max: Double): DoubleArgumentType = DoubleArgumentType.doubleArg(min, max)
    fun float(min: Float): FloatArgumentType = FloatArgumentType.floatArg(min)
    fun float(min: Float, max: Float): FloatArgumentType = FloatArgumentType.floatArg(min, max)
    fun integer(min: Int): IntegerArgumentType = IntegerArgumentType.integer(min)
    fun integer(min: Int, max: Int): IntegerArgumentType = IntegerArgumentType.integer(min, max)
    fun long(min: Long): LongArgumentType = LongArgumentType.longArg(min)
    fun long(min: Long, max: Long): LongArgumentType = LongArgumentType.longArg(min, max)
}