package com.worter

import org.junit.Assert.*

import org.junit.Test

class TranslationManagerTest {
    private fun assertCorrectTranslation(str: String, translation: TranslationData) {
        assertEquals(translation, TranslationManager.germanToPolish(str))
    }

    @Test
    fun translationShouldFetchMeaningsOnlyFromFirstDiv() {
        val str = "liegen"
        val translation = TranslationData(
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
        assertCorrectTranslation(str, translation)
    }

    @Test
    fun translationShouldFetchSentencesOnlyFromFirstDiv() {
        val str = "schwanger"
        val translation = TranslationData(
            meanings = listOf("ciężarna",
                "w ciąży"),
            sentences = listOf("Wer ist schwanger?")
        )
        assertCorrectTranslation(str, translation)
    }
    @Test
    fun translationOfGarbageShouldReturnEmptyTranslationData() {
        val empty = TranslationData(listOf(), listOf())
        assertEquals(empty, TranslationManager.germanToPolish("ASDFQWERTY"))
    }

    @Test
    fun translationOfWordWithTypoShouldReturnEmptyTranslationData() {
        val empty = TranslationData(listOf(), listOf())
        assertEquals(empty, TranslationManager.germanToPolish("chwanger"))
    }
}