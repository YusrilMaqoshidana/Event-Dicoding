package id.usereal.eventdicoding.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM eventsTable WHERE isActive = 1 ORDER BY date(beginTime) DESC")
    suspend fun getEventsActive(): List<EventEntity>

    @Query("SELECT * FROM eventsTable WHERE isActive = 0 ORDER BY date(beginTime) DESC")
    suspend fun getEventsNotActive(): List<EventEntity>


    @Query("SELECT * FROM eventsTable WHERE id IN (SELECT id FROM favoriteTable)")
    fun getFavoriteEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(eventsTable: List<EventEntity>)


    @Query("DELETE FROM eventsTable WHERE isActive = 0")
    suspend fun deleteFinishedEvents(): Int

    @Query("DELETE FROM eventsTable WHERE isActive = 1")
    suspend fun deleteUpcomingEvents(): Int


    @Query("SELECT * FROM eventsTable WHERE isActive = :isActive AND name LIKE '%' || :query || '%' ORDER BY date(beginTime) ASC")
    fun searchEvents(query: String, isActive: Int): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    @Delete
    suspend fun deleteFavoriteEvent(favorite: FavoriteEntity): Int
    @Query("SELECT * FROM eventsTable WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): EventEntity

    @Query("SELECT EXISTS(SELECT * FROM favoriteTable WHERE id = :eventId)")
    fun isEventFavorite(eventId: String): Flow<Boolean>
}
