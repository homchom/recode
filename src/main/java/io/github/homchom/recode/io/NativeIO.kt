@file:JvmName("NativeIO")

package io.github.homchom.recode.io

import io.github.homchom.recode.mc
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

/**
 * Opens a native file picker, blocks the current thread until the dialog closes, and returns the [Path] to
 * the picked file if one was picked.
 *
 * @param defaultPath The path to start at. If `null`, the operating system will choose.
 * @param filter The [FileFilter] to apply. If `null`, all files will be permitted.
 *
 * @throws IOException If the native file dialog errors for any reason.
 */
@JvmOverloads
fun pickFile(title: String? = null, defaultPath: Path? = null, filter: FileFilter? = null) =
    openFileDialog(filter?.patterns) { filterBuffer ->
        TinyFileDialogs.tinyfd_openFileDialog(
            title,
            defaultPath?.pathString,
            filterBuffer,
            filter?.description,
            false
        )?.let(::Path)
    }

/**
 * Opens a native file picker, blocks the current thread until the dialog closes, and returns the [Path]s to
 * the picked files if any were picked. The opened file picker can pick multiple files.
 *
 * @see pickFile
 */
@JvmOverloads
fun pickMultipleFiles(title: String? = null, defaultPath: Path? = null, filter: FileFilter? = null) =
    openFileDialog(filter?.patterns) { filterBuffer ->
        val joinedPaths = TinyFileDialogs.tinyfd_openFileDialog(
            title,
            defaultPath?.pathString,
            filterBuffer,
            filter?.description,
            true
        )
        joinedPaths?.split('|')?.map(::Path)
    }

/**
 * A file filter, to be passed to [pickFile] or [pickMultipleFiles].
 */
data class FileFilter(val patterns: List<String>, val description: String? = null)

private inline fun <R : Any> openFileDialog(
    filterPatterns: List<String>?,
    nativeDialogFunction: (PointerBuffer?) -> R?
): R? {
    MemoryStack.stackPush().use { stack ->
        val filterPatternBuffer = filterPatterns?.let { patterns ->
            val buffer = stack.mallocPointer(patterns.size)
            for (pattern in patterns) buffer.put(stack.UTF8(pattern))
            buffer.flip()
        }
        if (mc.isRunning) mc.mouseHandler.releaseMouse()
        return nativeDialogFunction(filterPatternBuffer)
    }
}