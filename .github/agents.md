# Simple Finance — Agent Context

> Este archivo provee contexto para agentes de IA (GitHub Copilot, Claude, etc.) que retomen el desarrollo del proyecto.

---

## Estado actual del proyecto

**Versión:** 1.1.1 (versionCode 4)  
**Plataformas:** Android + iOS (Kotlin Multiplatform + Compose Multiplatform)  
**Base de datos:** Room local, sin backend

### Funcionalidades implementadas
- [x] Registro de gastos con categoría y método de pago
- [x] Presupuesto mensual por categoría
- [x] Registro de deudas con cuotas (crédito/préstamo)
- [x] Filtro por mes en pantalla de gastos
- [x] Gráfico de torta de deudas por fuente de pago
- [x] Pantalla de exportación a IA (tab "IA"):
  - Agrupa gastos por mes → categoría → ítem individual con método de pago
  - Incluye deudas activas con cuotas pendientes
  - Genera prompt estructurado en español
  - Botón "Copiar prompt" (usa `LocalClipboardManager`)

---

## Arquitectura rápida

```
UI (Compose) → ViewModel (StateFlow) → UseCase / Repository → RoomDataSource → Room DB
```

DI con Koin. Todos los módulos en `composeApp/src/commonMain/.../framework/koin/di.kt`.

---

## Archivos clave para continuar

| Archivo | Para qué sirve |
|---|---|
| `framework/koin/di.kt` | Agregar nuevos ViewModels, use cases, repositories |
| `ui/home/navigation/Destinations.kt` | Agregar tabs al bottom nav |
| `ui/home/navigation/ItemsBottomNav.kt` | Ícono + título de cada tab |
| `ui/home/navigation/HomeNavigation.kt` | Registrar composable de nuevo tab |
| `ui/home/HomeScreen.kt` | Controlar visibilidad del date filter por tab |
| `ui/home/composables/BottomNavigation.kt` | Lista de tabs (`menuItems`) |
| `domain/models/` | Modelos de dominio puros |
| `framework/room/model/` | Entidades Room + funciones `toDomain()` / `toEntity()` |

---

## Gotchas conocidos

1. **`ExpenseEntity.toDomain()` no resuelve la categoría completa** — solo guarda el `categoryId`. Para obtener `title` y `budget` hay que llamar `categoryRepository.getById(categoryId)` por separado.

2. **Categorías son instancias mensuales** — `Category` tiene `date: "MM/YYYY"` y un `userCategoryId` que apunta a la `UserCategory` base. Al crear nuevos gastos, el `categoryId` es el de la instancia mensual.

3. **Crear gasto con CREDIT/LOAN crea automáticamente una Deuda** — ver `ExpenseRepository.createExpense()`.

4. **`DateManager`** es un singleton global que controla el mes/año seleccionado en la pantalla de gastos. Las pantallas de Balance y Export ignoran este filtro.

5. **Fechas en ISO**: `expense.date = "YYYY-MM-DDTHH:MM:SS"`. Extraer mes/año con `date.substringBefore('T').split("-")`.

---

## Ideas / próximas funcionalidades sugeridas

- [ ] **Filtro de fechas en Export** — permitir seleccionar rango de meses a exportar
- [ ] **Compartir prompt** como texto (Share Sheet nativo) además de copiar al portapapeles
- [ ] **Gráfico de barras** de gastos por mes en la pantalla de Balance o Export
- [ ] **Notificaciones** de gastos que exceden el presupuesto
- [ ] **Backup / export a CSV** de todos los datos
- [ ] **Soporte multimoneda** (actualmente ARS implícito)
- [ ] **Widget** de resumen rápido (Android)
- [ ] **Estadísticas** de categoría individual a lo largo del tiempo (en CategoryScreen)
- [ ] **Metas de ahorro** con progreso visual

---

## Cómo agregar un nuevo tab al bottom nav

1. `Destinations.kt` → agregar `data object NuevoTab : HomeDestination()`
2. `ItemsBottomNav.kt` → agregar `data object NuevoTab: ItemsBottomNav(icon, title, route)`
3. `BottomNavigation.kt` → agregar a `menuItems`
4. `HomeNavigation.kt` → agregar `composable<HomeDestination.NuevoTab> { NuevoTabScreen() }`
5. `HomeScreen.kt` → agregar `val isOnNuevoTab = navBackStackEntry.hasRoute(HomeDestination.NuevoTab)` y excluirlo del date filter si corresponde
6. `di.kt` → registrar el ViewModel con `viewModel { NuevoTabViewModel(get()) }`

## Cómo agregar un nuevo destino de nivel superior (drill-in)

1. `AppDestinations.kt` → agregar `data class NuevoDestino(val id: Int) : AppDestinations()`
2. `AppNavigation.kt` → agregar `animatedComposable<AppDestinations.NuevoDestino> { ... }`
3. Desde HomeNavigation pasar callback `onNavigateOuterHome(AppDestinations.NuevoDestino(id))`
