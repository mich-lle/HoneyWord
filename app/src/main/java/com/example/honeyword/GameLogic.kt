package com.example.honeyword

import android.content.Context
import java.time.LocalDate
import java.util.Locale

data class Puzzle(
    val letters: List<Char>, // size 7; index 0 is the center/required letter
    val allowedWords: Set<String>,
    val pangrams: Set<String>,
    val maxScore: Int
)

object WordUtils {
    fun loadWordList(context: Context): Set<String> {
        // Loads a lowercase word list from assets/words.txt
        return try {
            context.assets.open("words.txt").bufferedReader().useLines { seq ->
                seq.map { it.trim().lowercase(Locale.ROOT) }
                    .filter { it.length >= 3 && it.all { ch -> ch.isLetter() } }
                    .toSet()
            }
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun isValidForBee(word: String, seven: Set<Char>, required: Char): Boolean {
        if (word.length < 4) return false
        if (!word.contains(required)) return false
        return word.all { it in seven }
    }

    fun score(word: String, isPangram: Boolean): Int {
        return when {
            word.length == 4 -> 1
            else -> word.length + if (isPangram) 7 else 0
        }
    }
}

class PuzzleGenerator(private val words: Set<String>) {
    private val candidates = words.filter { it.length >= 7 }.toSet()

    fun generate(seed: Int? = null): Puzzle? {
        val rng = java.util.Random((seed ?: LocalDate.now().toString().hashCode()).toLong())

        val tries = candidates.shuffled(rng).take(2000)
        for (w in tries) {
            val unique = w.toSet()
            if (unique.size == 7) {
                val letters = unique.toList().sorted() // stable order; we'll shuffle later
                val center = letters[rng.nextInt(letters.size)]
                val seven = letters.toMutableList()
                // Put the center letter at index 0
                seven.remove(center)
                seven.add(0, center)

                // Collect allowed words
                val sevenSet = seven.toSet()
                val allowed = words.filter { WordUtils.isValidForBee(it, sevenSet, center) }.toSet()
                if (allowed.size >= 20) {
                    val pangrams = allowed.filter { it.toSet().size == 7 }.toSet()
                    val maxScore = allowed.sumOf { WordUtils.score(it, it in pangrams) }
                    return Puzzle(seven, allowed, pangrams, maxScore)
                }
            }
        }
        return null
    }
}