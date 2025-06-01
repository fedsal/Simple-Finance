package org.fedsal.finance.domain.models


object DefaultCategories {
    val MARKET = Category(
        title = "Mercado",
        budget = 200000.0,
        iconId = AppIcons.SHOPPING_CART.name,
        color = AppColors.ORANGE.hexString
    )
    val FIXED_EXPENSES = Category(
        title = "Gastos fijos",
        budget = 100000.0,
        iconId = AppIcons.PIN.name,
        color = AppColors.CYAN.hexString
    )
    val TRANSPORT = Category(
        title = "Transporte",
        budget = 100000.0,
        iconId = AppIcons.CAR.name,
        color = AppColors.PURPLE.hexString
    )
    val RENT = Category(
        title = "Alquiler",
        budget = 500000.0,
        iconId = AppIcons.HOME.name,
        color = AppColors.RED.hexString
    )
    val FUN = Category(
        title = "Diversión",
        budget = 200000.0,
        iconId = AppIcons.SHOPPING_BAG.name,
        color = AppColors.GREEN.hexString
    )
}
