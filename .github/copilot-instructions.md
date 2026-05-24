# Simple Finance — Copilot Instructions

## Project Overview
Expense and debt tracking app built with **Kotlin Multiplatform (KMP)** targeting Android and iOS.
UI is fully shared via **Jetpack Compose Multiplatform**. All data is stored locally — no network layer.

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose Multiplatform |
| Architecture | Clean Architecture (UI → Domain → Data → Framework) |
| DI | Koin (`koin-compose`, `koin-compose-viewmodel`) |
| Database | Room (multiplatform via `androidx.room`) |
| Navigation | `androidx.navigation.compose` (type-safe routes via `@Serializable`) |
| Async | Kotlin Coroutines + Flow |
| Date/Time | `kotlinx-datetime` |

---

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/org/fedsal/finance/
│   ├── data/               # Repositories + LocalDataSource interfaces
│   │   ├── category/
│   │   ├── debt/
│   │   ├── expense/
│   │   ├── paymentmethod/
│   │   └── usercategory/
│   ├── domain/
│   │   ├── models/         # Pure Kotlin data classes
│   │   └── usecases/       # BaseUseCase subclasses
│   ├── framework/
│   │   ├── koin/di.kt      # All Koin module definitions
│   │   └── room/           # DAOs, Entities, RoomDataSource implementations
│   └── ui/
│       ├── home/           # Main bottom-nav host
│       │   ├── allcategories/   # ExpensesScreen (tab 1)
│       │   ├── balance/         # BalanceScreen (tab 2)
│       │   ├── export/          # ExportScreen (tab 3 — AI prompt export)
│       │   ├── composables/     # BottomNavigation, ButtonBottomSheet
│       │   └── navigation/      # HomeDestination, HomeNavigation, ItemsBottomNav
│       ├── main/           # App entry, AppNavigation (top-level nav)
│       ├── categoryExpenses/    # CategoryScreen (drill-in from tab 1)
│       ├── debtdetail/          # DebtDetailScreen (drill-in from tab 2)
│       └── common/         # Shared composables, theme, utils, DateManager
├── androidMain/            # Android-specific: MainActivity, DatabaseBuilder, DI platform module
├── iosMain/                # iOS-specific: MainViewController, DatabaseBuilder, DI platform module
└── nativeMain/             # Native-shared: CurrencyFormat, utils (locale/month name)
```

---

## Domain Models

### `Expense`
```kotlin
data class Expense(id, title, amount, date, description, category: Category, paymentMethod: PaymentMethod)
```
- `date` is ISO format: `"YYYY-MM-DDTHH:MM:SS"` (use `convertToIso` / `convertFromIso` from `utils.kt`)
- Creating an expense with a CREDIT or LOAN payment method **automatically creates a linked `Debt`**

### `Debt`
```kotlin
data class Debt(id, title, amount, date, category, installments, paidInstallments, paymentMethod, description, expenseId)
```
- `installments` / `paidInstallments` track monthly payment progress
- `expenseId` links back to the originating expense (nullable)

### `Category` vs `UserCategory`
- `UserCategory`: the user-defined template (title, budget, iconId, color). Persists across months.
- `Category`: a monthly instance of a UserCategory (adds `date: "MM/YYYY"` and its own `id`).
- Expenses store a **monthly `Category.id`** — when reading back via `toDomain()`, only `categoryId` is populated; title/budget are **empty**. Always call `categoryRepository.getById(categoryId)` to resolve the full data.

### `PaymentMethod`
```kotlin
data class PaymentMethod(id, name, type: PaymentMethodType, iconId, color)
enum class PaymentMethodType { CASH, CREDIT, LOAN }
```

---

## Architecture Patterns

### Use Cases
Extend `BaseUseCase<Params, ReturnType>`. Invoke via:
```kotlin
useCase.invoke(params, onError = { ... }, onSuccess = { result -> ... })
```
For Flow-returning use cases, collect inside a `viewModelScope.launch` block after receiving the Flow in `onSuccess`.

### ViewModels
- Use `MutableStateFlow<UiState>` + expose as `StateFlow`
- Inject via Koin: registered with `viewModel { MyViewModel(get(), get()) }` in `di.kt`
- Always add new ViewModels to `provideViewModelModule` in `framework/koin/di.kt`

### Navigation
**Top-level** (`AppNavigation`): `Home` → `Category(id)` → `DebtDetail(id)`  
**Home tabs** (`HomeNavigation`): `Expenses` ↔ `Balance` ↔ `Export`

To add a new bottom-nav tab:
1. Add destination to `HomeDestination` sealed class
2. Add item to `ItemsBottomNav` sealed class (with icon + title)
3. Add to `menuItems` list in `BottomNavigation.kt`
4. Add `composable<HomeDestination.NewTab>` in `HomeNavigation.kt`
5. Hide date filter in `HomeScreen.kt` if needed (`isOnBalance || isOnExport || isOnNewTab`)

### Dependency Injection (Koin)
All modules are in `framework/koin/di.kt`:
- `platformModule()` — expect/actual per platform (provides `RoomDatabase.Builder`)
- `provideDatabaseModule` — Room DAOs
- `provideDataSourceModule` — binds `RoomDataSource` → `LocalDataSource` interface
- `provideRepositoryModule` — repositories (singletons)
- `useCaseModule` — use cases (singletons)
- `provideViewModelModule` — ViewModels

---

## Key Conventions

- **Currency formatting**: use `Double.formatDecimal()` from `ui/common/utils.kt` (formats with `.` thousands and `,` decimals, ARS style)
- **Date display**: `convertFromIso(date)` → `"DDMM"` string; `convertToIso(input)` → ISO string
- **Expense dates**: always ISO `"YYYY-MM-DDTHH:MM:SS"`. Month/year extracted as `date.substringBefore('T').split("-")[0..1]`
- **Icons**: use `AppIcons` enum + `getIcon(name)` helper. Available: CARD, CASH, SHOPPING_CART, PIN, CAR, HOME, SHOPPING_BAG, PERSON, BANK
- **Colors**: use `AppColors` enum + `hexToColor(hex)` helper. Colors stored as hex strings in DB
- **Month filtering**: global state via `DateManager` singleton (selectedMonth, selectedYear flows)
- **Platform-specific code**: use `expect/actual`. Existing examples: `CurrencyFormat`, `getLocalizedMonthName`, `getCurrentLocale`
- **Compose clipboard**: use `LocalClipboardManager.current` — available in commonMain, no expect/actual needed

---

## Current Features

| Screen | Description |
|---|---|
| **Expenses** (tab 1) | Lists all expense categories for selected month. Date filter in top bar. FAB opens bottom sheet to add expense/debt/category/payment method |
| **Balance** (tab 2) | Pie chart of active debts by payment method. Taps drill into DebtDetailScreen |
| **IA Export** (tab 3) | Summarizes all expenses grouped by month → category → individual items (with payment method). Builds structured AI prompt. Copy button copies to clipboard |
| **CategoryScreen** | Drill-in showing all expenses for a specific category in the selected month |
| **DebtDetailScreen** | Drill-in showing all debts for a specific payment method |

---

## Important Notes

- `ExpenseEntity.toDomain()` reconstructs `Category` with **only `id` filled** — always resolve title/budget via `categoryRepository.getById(categoryId)` when you need the full category data outside of `GetExpensesByCategoryUseCase`
- Deleting an expense with CREDIT/LOAN payment method also deletes the linked debt (`ExpenseRepository.deleteExpense`)
- Creating a monthly `Category` instance is handled by `CategoryRepository.read()` — it auto-creates instances for missing months when the view is loaded
- The app language is **Spanish** (Argentina). Keep UI strings in Spanish
