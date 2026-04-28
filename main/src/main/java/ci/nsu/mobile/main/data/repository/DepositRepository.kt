package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.db.DepositCalculation
import kotlinx.coroutines.flow.Flow

interface DepositRepository {
    fun getCalculationsForUser(userId: Long): Flow<List<DepositCalculation>>
    suspend fun insert(calculation: DepositCalculation)
    suspend fun delete(calculation: DepositCalculation)
}
