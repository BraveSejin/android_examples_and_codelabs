package com.sejin.room_with_a_view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "word_table")
// 이렇게 하면 word가 기본 키 역할을 한다.
data class Word(@PrimaryKey @ColumnInfo(name = "word") val word: String)