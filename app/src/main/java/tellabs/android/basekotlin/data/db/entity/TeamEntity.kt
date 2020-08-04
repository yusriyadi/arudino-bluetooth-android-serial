package tellabs.android.basekotlin.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team")
data class TeamEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val teamId: Int,
    val teamName: String,
    val teamImage: String,
    val teamDescription: String,
    val stadiumName: String
)