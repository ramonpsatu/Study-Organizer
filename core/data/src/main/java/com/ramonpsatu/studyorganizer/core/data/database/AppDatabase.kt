package com.ramonpsatu.studyorganizer.core.data.database

import android.content.Context
import androidx.room.*
import com.ramonpsatu.studyorganizer.core.data.database.dao.RevisionDAO
import com.ramonpsatu.studyorganizer.core.data.database.dao.SubjectDAO
import com.ramonpsatu.studyorganizer.core.data.database.dao.TopicDAO
import com.ramonpsatu.studyorganizer.core.data.database.entity.Revision
import com.ramonpsatu.studyorganizer.core.data.database.entity.Subject
import com.ramonpsatu.studyorganizer.core.data.database.entity.Topic

@Database(
    entities = [
        Subject::class, Topic::class, Revision::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DaysOfWeekConverter::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun revisionDAO(): RevisionDAO
    abstract fun subjectDAO(): SubjectDAO
    abstract fun topicDAO(): TopicDAO


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATA_BASE_NAME
                )
                    .build()

                INSTANCE = instance
                instance
            }


        }

        private const val DATA_BASE_NAME = "App-database.db"

    }


}