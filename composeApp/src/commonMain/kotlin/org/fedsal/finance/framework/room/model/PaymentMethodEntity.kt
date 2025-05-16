package org.fedsal.finance.framework.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val type: PaymentMethodType,
    val iconId: String,
    val color: String,
)

fun PaymentMethodEntity.toDomain(): PaymentMethod {
    return PaymentMethod(
        id = id,
        name = name,
        type = type,
        iconId = iconId,
        color = color
    )
}

fun PaymentMethod.toEntity(): PaymentMethodEntity {
    return PaymentMethodEntity(
        id = id,
        name = name,
        type = type,
        iconId = iconId,
        color = color
    )
}
