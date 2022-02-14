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

//TODO(picking elements by order doesn't work)
object TranslationManager {

    fun germanToPolish(word: String): TranslationData {
        return skrape(HttpFetcher) {
            request {
                url = "https://www.diki.pl/slownik-niemieckiego?q=${word}"
            }
            extractIt {
                htmlDocument {
                    it.word = try {
                        findAll {
                            div {
                                withClass = "hws"
                                findAll {
                                    h1 {
                                        findAll {
                                            span {
                                                withClass = "hw"
                                                findAll{
                                                    this
                                                }.map {
                                                    if (it.children.isEmpty()) {
                                                        it.ownText
                                                    } else {
                                                        it.children[0].ownText
                                                    }
                                                }.sortedBy { levenshtein(it, word) }[0]
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch(exception: Exception) {
                        word
                    }

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

fun levenshtein(lhs : CharSequence, rhs : CharSequence) : Int {
    val lhsLength = lhs.length
    val rhsLength = rhs.length

    var cost = IntArray(lhsLength + 1) { it }
    var newCost = IntArray(lhsLength + 1) { 0 }

    for (i in 1..rhsLength) {
        newCost[0] = i

        for (j in 1..lhsLength) {
            val editCost= if(lhs[j - 1] == rhs[i - 1]) 0 else 1

            val costReplace = cost[j - 1] + editCost
            val costInsert = cost[j] + 1
            val costDelete = newCost[j - 1] + 1

            newCost[j] = minOf(costInsert, costDelete, costReplace)
        }

        val swap = cost
        cost = newCost
        newCost = swap
    }

    return cost[lhsLength]
}
