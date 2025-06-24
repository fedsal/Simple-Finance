package org.fedsal.finance.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.framework.room.model.PaymentMethodEntity

@Dao
interface PaymentMethodDao {
    @Insert
    suspend fun create(paymentMethod: PaymentMethodEntity)

    @Query("SELECT * FROM payment_methods ORDER BY name ASC")
    suspend fun readAll(): List<PaymentMethodEntity>

    @Query("SELECT * FROM payment_methods ORDER BY name ASC")
    fun readAllWithFlow(): Flow<List<PaymentMethodEntity>>

    @Update
    suspend fun update(paymentMethod: PaymentMethodEntity)

    @Delete
    suspend fun delete(entity: PaymentMethodEntity)

    @Query("SELECT * FROM payment_methods WHERE id = :id")
    fun getPaymentMethodById(id: Int): Flow<PaymentMethodEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg methods: PaymentMethodEntity)
}
