package org.fedsal.finance.framework.room.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.PaymentMethod

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // Payment method
    @TypeConverter
    fun fromPaymentMethod(paymentMethod: PaymentMethod): String {
        return json.encodeToString(PaymentMethod.serializer(), paymentMethod)
    }

    @TypeConverter
    fun toPaymentMethod(data: String): PaymentMethod {
        return json.decodeFromString(PaymentMethod.serializer(), data)
    }

    // Category
    @TypeConverter
    fun fromCategory(category: Category): String {
        return json.encodeToString(Category.serializer(), category)
    }

    @TypeConverter
    fun toCategory(data: String): Category {
        return json.decodeFromString(Category.serializer(), data)
    }
}
