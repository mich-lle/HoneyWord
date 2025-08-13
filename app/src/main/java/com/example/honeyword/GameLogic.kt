package com.example.honeyword

import android.content.Context
import java.time.LocalDate
import java.util.Locale

data class Puzzle(
    /** size 7; index 0 is the center/required letter */
    val letters: List<Char>,
    val allowedWords: Set<String>,
    /** words that use all 7 puzzle letters at least once */
    val starweaves: Set<String>,
    val maxScore: Int
) {
    /** Backward-compat alias for older code that still refers to pangrams */
    @Deprecated("Use starweaves instead")
    val pangrams: Set<String> get() = starweaves
}

object WordUtils {

    /** Load lowercase words from assets/words.txt (letters only). */
    fun loadWordList(context: Context): Set<String> {
        return try {
            context.assets.open("words.txt")
                .bufferedReader()
                .useLines { seq ->
                    seq.map { it.trim().lowercase(Locale.ROOT) }
                        .filter { it.length >= 2 && it.all { ch -> ch in 'a'..'z' } }
                        .toSet()
                }
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * A word is valid for this 7-letter puzzle if:
     * - length >= minLen (default 4)
     * - contains the required (center) letter
     * - every character is one of the 7 puzzle letters
     */
    fun isValidForPuzzle(
        word: String,
        seven: Set<Char>,
        required: Char,
        minLen: Int = 4
    ): Boolean {
        if (word.length < minLen) return false
        if (!word.contains(required)) return false
        if (!word.all { it in seven }) return false
        return true
    }

    /** Backward-compat: delegate to isValidForPuzzle. */
    @Deprecated("Use isValidForPuzzle instead")
    fun isValidForBee(word: String, seven: Set<Char>, required: Char): Boolean =
        isValidForPuzzle(word, seven, required, minLen = 4)

    /** True if the word uses all 7 puzzle letters at least once. */
    fun isStarweave(word: String, seven: Set<Char>): Boolean {
        val used = word.toSet()
        return seven.all { it in used }
    }

    /** NYT-style scoring: 4 letters = 1 pt; else length + 7 bonus if Starweave. */
    fun score(word: String, isStarweave: Boolean): Int {
        return if (word.length == 4) 1 else word.length + if (isStarweave) 7 else 0
    }
}

class PuzzleGenerator(private val words: Set<String>) {

    /**
     * Generate a puzzle by picking a seed with exactly 7 unique letters,
     * then choosing a center that yields a sufficiently rich answer set.
     */
    fun generate(
        seed: Int? = null,
        minAnswers: Int = 20,
        maxTries: Int = 2000
    ): Puzzle? {
        val rng = java.util.Random((seed ?: LocalDate.now().toString().hashCode()).toLong())

        // Candidate seeds: words that contain exactly 7 unique letters (any length)
        val candidates = words.asSequence()
            .map { it to it.toSet() }
            .filter { (_, uniq) -> uniq.size == 7 }
            .map { it.first }
            .shuffled(rng)
            .take(maxTries)
            .toList()

        for (w in candidates) {
            val sevenSet = w.toSet()
            val lettersSorted = sevenSet.toList().sorted()

            // Try each of the 7 as the center; stop at the first rich configuration
            for (center in lettersSorted) {
                val sevenOrdered = buildList(7) {
                    add(center)                          // index 0 = center
                    addAll(lettersSorted.filter { it != center })
                }
                val seven = sevenOrdered.toSet()

                val allowed = words.asSequence()
                    .filter { WordUtils.isValidForPuzzle(it, seven, center, minLen = 4) }
                    .toSet()

                if (allowed.size >= minAnswers) {
                    val starweaves = allowed.filter { WordUtils.isStarweave(it, seven) }.toSet()
                    val maxScore = allowed.sumOf { WordUtils.score(it, it in starweaves) }
                    return Puzzle(
                        letters = sevenOrdered,
                        allowedWords = allowed,
                        starweaves = starweaves,
                        maxScore = maxScore
                    )
                }
            }
        }
        return null
    }
}
