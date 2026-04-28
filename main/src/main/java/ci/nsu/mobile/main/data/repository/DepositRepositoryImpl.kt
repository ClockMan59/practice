package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.db.DepositCalculation
import ci.nsu.mobile.main.data.db.DepositDao
import kotlinx.coroutines.flow.Flow

class DepositRepositoryImpl(
    private val depositDao: DepositDao
) : DepositRepository {

    override fun getCalculationsForUser(userId: Long): Flow<List<DepositCalculation>> {
        return depositDao.getCalculationsForUser(userId)
    }

    override suspend fun insert(calculation: DepositCalculation) {
        depositDao.insertCalculation(calculation)
    }

    override suspend fun delete(calculation: DepositCalculation) {
        depositDao.deleteCalculation(calculation)
    }
}
