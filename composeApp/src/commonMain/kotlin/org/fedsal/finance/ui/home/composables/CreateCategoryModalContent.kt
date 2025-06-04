package org.fedsal.finance.ui.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.ui.common.ExpenseDefaults
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.common.opaqueColor
import org.fedsal.finance.ui.common.rememberCurrencyVisualTransformation

@Composable
fun CreateCategoryModalContent(
) {
    Box(Modifier.fillMaxSize()) {

        var categoryTitle by remember { mutableStateOf("") }
        var categoryBudget by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf(AppColors.RED) }
        var selectedIcon by remember { mutableStateOf(AppIcons.SHOPPING_BAG) }

        var titleError by remember { mutableStateOf(false) }
        var budgetError by remember { mutableStateOf(false) }

        // Save button
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = "Done",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .padding(end = 24.dp)
                .clickable {
                    if (categoryTitle.isBlank()) { titleError = true }
                    else if (categoryBudget.isBlank()) { budgetError = true }
                    else {

                    }
                },
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Agregar una categoria",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(24.dp))

            // Title section
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                label = "Titulo",
                value = categoryTitle,
                onValueChange = {
                    titleError = false
                    categoryTitle = it
                },
                placeHolder = "Ingrese el titulo",
                isError = titleError,
                keyboardType = KeyboardType.Text
            )
            Spacer(Modifier.height(16.dp))

            // Category budget
            val currencyVisualTransformation =
                rememberCurrencyVisualTransformation(currency = "USD")
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                label = "Presupuesto",
                value = categoryBudget,
                onValueChange = { newValue ->
                    budgetError = false
                    val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
                    if (trimmed.isEmpty() || trimmed.toInt() <= ExpenseDefaults.MAX_EXPENSE_VALUE) {
                        categoryBudget = trimmed
                    }
                },
                placeHolder = "$ --- ---",
                visualTransformation = currencyVisualTransformation,
                keyboardType = KeyboardType.Number,
                isError = budgetError
            )
            Spacer(Modifier.height(16.dp))

            // Icon Section

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Customizar icono:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(16.dp))

            CategoryIcon(
                modifier = Modifier.size(50.dp),
                iconTint = hexToColor(selectedColor.hexString),
                icon = getIcon(selectedIcon.name)
            )
            Spacer(Modifier.height(16.dp))

            // Color selector
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Seleccione un color:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(AppColors.entries.toTypedArray()) { color ->
                    Box(
                        modifier = Modifier.size(32.dp)
                            .background(shape = CircleShape, color = hexToColor(color.hexString))
                            .border(
                                width = 3.dp,
                                color = if (selectedColor == color) MaterialTheme.colorScheme.onSurface
                                else Color.Black.copy(alpha = .5f),
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                            }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // Icon selector
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Seleccione un icono:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(AppIcons.entries.toTypedArray()) {
                    val highlightColor = if (selectedIcon == it) MaterialTheme.colorScheme.onSurface
                    else opaqueColor(MaterialTheme.colorScheme.onSurface)
                    Box(
                        modifier = Modifier.size(40.dp)
                            .border(
                                width = 3.dp,
                                color = highlightColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                            .clickable {
                                selectedIcon = it
                            }
                    ) {
                        Icon(
                            imageVector = getIcon(it.name),
                            contentDescription = it.name,
                            modifier = Modifier.fillMaxSize(),
                            tint = highlightColor
                        )
                    }
                }
            }
        }
    }
}
