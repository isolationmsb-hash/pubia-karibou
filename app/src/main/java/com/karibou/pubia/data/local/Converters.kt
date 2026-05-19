package com.karibou.pubia.data.local

import androidx.room.TypeConverter
import com.karibou.pubia.domain.model.AdFormat
import com.karibou.pubia.domain.model.AdProjectStatus

/**
 * TypeConverters Room — serialise les enums en String pour SQLite.
 */
class Converters {

    @TypeConverter
    fun statusToString(s: AdProjectStatus): String = s.name

    @TypeConverter
    fun stringToStatus(s: String): AdProjectStatus = AdProjectStatus.valueOf(s)

    @TypeConverter
    fun formatToString(f: AdFormat): String = f.name

    @TypeConverter
    fun stringToFormat(s: String): AdFormat = AdFormat.valueOf(s)
}
