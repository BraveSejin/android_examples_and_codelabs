package com.sejin.room_with_a_view

import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()

    suspend fun insert (word: Word){
        wordDao.insert(word)
    }
}