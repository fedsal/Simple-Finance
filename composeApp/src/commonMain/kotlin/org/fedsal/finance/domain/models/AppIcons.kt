package org.fedsal.finance.domain.models

enum class AppIcons {
    CARD, CASH, SHOPPING_CART, PIN, CAR, HOME, SHOPPING_BAG, PERSON, BANK;

    companion object {
        fun fromName(name: String): AppIcons? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

enum class AppColors(
    val hexString: String
) {
    ORANGE("#FF5100"),
    CYAN("#00F6FF"),
    GREEN("#00FF04"),
    RED("FF0000"),
    PURPLE("9D00FF");

    companion object {
        fun fromHex(hex: String): AppColors? {
            return entries.find { it.hexString.equals(hex, ignoreCase = true) }
        }
    }
}
