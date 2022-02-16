package com.worter

import org.junit.Assert.*

import org.junit.Test

class TranslationManagerTest {
    private fun assertCorrectTranslation(str: String, translation: TranslationData) {
        assertEquals(translation, TranslationManager.germanToPolish(str))
    }

    @Test
    fun translationShouldFetchMeaningsOnlyFromFirstDiv() {
        val word = "liegen"
        val translation = TranslationData(
            word,
            meanings = listOf(
                "leżeć",
                "być położonym",
                "znajdować się",
                "podobać się",
                "zależeć",
                "leżeć (odpowiedzialność, przyczyna)"
            ),
            sentences = listOf(
                "Das Kissen liegt auf dem Bett.",
                "Wo liegt mein Heft?",
                "Berlin liegt in Brandenburg.",
                "Wo liegt diese Straße?"
            )
        )
        assertCorrectTranslation(word, translation)
    }

    @Test
    fun translationShouldFetchSentencesOnlyFromFirstDiv() {
        val word = "schwanger"
        val translation = TranslationData(
            word,
            meanings = listOf("ciężarna",
                "w ciąży"),
            sentences = listOf("Wer ist schwanger?")
        )
        assertCorrectTranslation(word, translation)
    }

    @Test
    fun translationOfGarbageShouldReturnEmptyTranslationData() {
        val word = "ASDFQWERTY"
        val empty = TranslationData(word, listOf(), listOf())
        assertEquals(empty, TranslationManager.germanToPolish(word))
    }

    @Test
    fun translationOfWordWithTypoShouldReturnEmptyTranslationData() {
        val word = "chwanger"
        val empty = TranslationData(word, listOf(), listOf())
        assertEquals(empty, TranslationManager.germanToPolish(word))
    }
}