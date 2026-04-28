package ci.nsu.mobile.main.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query(
        "SELECT * FROM deposit_calculations " +
            "WHERE userId = :userId ORDER BY calculationDate DESC"
    )
    fun getCalculationsForUser(userId: Long): Flow<List<DepositCalculation>>

    @Insert
    suspend fun insertCalculation(calculation: DepositCalculation)

    @Delete
    suspend fun deleteCalculation(calculation: DepositCalculation)
}
