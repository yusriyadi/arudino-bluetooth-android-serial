package tellabs.android.basekotlin.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import tellabs.android.basekotlin.data.db.dao.TeamDao
import tellabs.android.basekotlin.data.db.entity.TeamEntity

@Database(
    entities = [TeamEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun teamDao() : TeamDao

}