package com.worter

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

data class TranslationData(
    var word: String = "",
    var meanings: List<String> = emptyList(),
    var sentences: List<String> = emptyList()
)

object TranslationManager {

    fun germanToPolish(word: String): TranslationData {
        return skrape(HttpFetcher) {
            request {
                url = "https://www.diki.pl/slownik-niemieckiego?q=${word.lowercase()}"
            }
            extractIt {
                htmlDocument {
                    it.word = try {
                        val span = "div.hws" {
                            findFirst { this }
                        }.children[0].children[0]
                        if (span.children.isEmpty()) {
                            span.ownText
                        } else {
                            span.children[0].ownText
                        }
                    } catch(exception: Exception) {
                        word
                    }

                    it.meanings = try {
                        "ol.foreignToNativeMeanings" {
                            findFirst { this }
                        }.findAll("li").asSequence().map {
                            it.children.filter { it1 ->
                                it1.tagName == "span" && it1.hasClass("hw")
                            }
                        }.flatten().map {
                            it.children[0].ownText + " " + it.ownText
                        }.map {
                            it.trim()
                        }.distinct().toList()
                    } catch (exception: Exception) {
                        listOf()
                    }

                    it.sentences = try {
                        "ol.foreignToNativeMeanings" {
                            findFirst { this }
                        }.findAll("div.exampleSentence").map {
                            it.ownText
                        }
                    } catch (exception: Exception) {
                        listOf()
                    }

                }
            }
        }
    }

}
