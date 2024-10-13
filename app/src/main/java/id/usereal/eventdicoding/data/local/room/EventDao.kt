package id.usereal.eventdicoding.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import id.usereal.eventdicoding.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM evententity WHERE isFavorite = 1")
    fun getAllFavorite(): LiveData<List<EventEntity>>

    @Query("DELETE FROM evententity WHERE isFavorite = 0")
    fun deleteAllNonFavorite()

    @Query("SELECT EXISTS(SELECT * FROM evententity WHERE id = :id AND isFavorite = 1)")
    fun isFavoriteEvent(id: Int): Boolean

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: EventEntity)
}