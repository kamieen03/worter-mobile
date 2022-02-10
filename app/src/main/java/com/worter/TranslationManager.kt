package com.worter

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

data class TranslationData(
    var meanings: List<String> = emptyList(),
    var sentences: List<String> = emptyList()
)

object TranslationManager {

    fun germanToPolish(str: String): TranslationData {
        return skrape(HttpFetcher) {
            request {
                url = "https://www.diki.pl/slownik-niemieckiego?q=${str}"
            }
            extractIt {
                htmlDocument {
                    it.meanings = try {
                        ".foreignToNativeMeanings" {
                            0 {
                                findAll {
                                    span {
                                        withClass = "hw"
                                        findAll {
                                            this
                                        }.map {
                                            it.children[0].ownText + " " + it.ownText
                                        }.map {
                                            it.trim()
                                        }.distinct()
                                    }
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        listOf()
                    }

                    if (it.meanings.isEmpty()) {
                        return@htmlDocument
                    }

                    it.sentences = try {
                        ".foreignToNativeMeanings" {
                            0 {
                                findAll {
                                    div {
                                        withClass = "exampleSentence"
                                        findAll {
                                            this
                                        }.map {
                                            it.ownText
                                        }
                                    }
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        listOf()
                    }

                }
            }
        }
    }

}
