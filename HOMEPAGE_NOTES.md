# WorkLog — Home Page Documentation

> Generated 2026-05-26. Covers the complete Home Screen — APIs, backend logic, mobile logic, color system, and "how to change X" cheat sheet.

---

## 1. Overview

The Home screen has 3 sections:

| Section | Component | Source data |
|---|---|---|
| **Teal header banner** | `MainHeaderContent` | `userInfo` (profile) |
| **Active Shift Card** (with map + Start/Stop button) | `CurrentShiftContent` | `currentRota` (today's shift) |
| **My Shifts table** (35 rows, today anchored at top) | `LazyColumn` of `UpcomingShiftCard` | `monthlyRotas` (rolling window) |

---

## 2. APIs Used

### 2.1 User Profile
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `https://mobile-api.gbspares.com/api/app/auth/user/profile` |
| Use case | `UserProfileUseCase.getUserProfile` |
| Returns | `UserInfo` — `firstName`, `displayName`, `branchName`, `floor`, `designation` |
| Used for | Greeting text, branch name on header, **`floor` for category check** |

### 2.2 Current Shift (today)
| Property | Value |
|---|---|
| Method | `GET` |
| URL | `https://mobile-api.gbspares.com/api/app/rota/auth-user/current` |
| Use case | `GetAuthUserRotaUseCase.getCurrentRota()` |
| Returns | `CurrentRotaResponse { has_current_rota, current_rota? }` |
| Used for | Active Shift Card (map, time range, branch, Start/Stop button) |

### 2.3 My Shifts (rolling 35-day window)
| Property | Value |
|---|---|
| Method | `POST` |
| URL | `https://mobile-api.gbspares.com/api/app/rota/month/auth-user` |
| Body (form-data) | `mode=custom&days=35` |
| Use case | `GetAuthUserRotaUseCase.getLastNDaysRota(35)` |
| Returns | `RotaResponse` with `window` metadata + `rotas[]` |
| Used for | The 35-row shift table |

**Response shape:**
```json
{
  "status": "success",
  "data": {
    "employee_id": 102,
    "window": {
      "today": "2026-05-26",
      "today_index": 23,
      "target": 35,
      "future_count": 12,
      "past_count": 23,
      "total_count": 35
    },
    "rotas": [
      {
        "id": 3107,
        "date": "2026-05-03",
        "shift_start_time": "08:00",
        "shift_end_time": "17:00",
        "shift_status": null,
        "shift_type": "evening",
        "short_code": "LD",
        "is_leave": false,
        "floor_name": "3F"
      },
      ...
    ]
  }
}
```

### 2.4 Start / Stop Shift
| Method | URL | Body |
|---|---|---|
| `POST` | `/api/app/rota/auth-user/start-shift` | `employee_id, latitude, longitude` |
| `POST` | `/api/app/rota/auth-user/end-shift`   | `employee_id, latitude, longitude` |

Triggered by the Start/Stop button on the Active Shift Card.

---

## 3. Backend Logic (mobile-api, Laravel)

### 3.1 Rolling-window algorithm (`AppRotaService::getAuthUserRollingWindowRotas`)

```
1. Fetch all FUTURE shifts (date >= today), ASC, limit = days + 7  (42 cap when days=35)
2. If future.count >= days  →  no past needed
   Else                     →  fetch (days − future.count) most recent past shifts (date < today, DESC)
3. Combine + sort ASC by date
4. Compute today_index = position of today in combined list
5. Return as JSON with window metadata
```

**Why ASC final order:**
The mobile uses `LazyColumn.scrollToItem(today_index)` to anchor today at the top of the viewport. Past is above today (scroll up), future is below today (scroll down) — like a calendar.

### 3.2 Mode dispatch (`RotaController::auth_user_monthly_rota`)

The same endpoint `/rota/month/auth-user` handles 3 modes via the `mode` param:

| `mode` | Extra params | Behavior |
|---|---|---|
| `custom` | `days=N` | Rolling window (Home screen) |
| `monthly` (default) | `month`, `year` | Full month (Rota screen) |
| any other | `start_date`, `end_date` | Custom date range |

### 3.3 Leave detection (`ShortCode::leaveCodes()`)

```php
public static function leaveCodes(): array {
    return Cache::remember('short_codes:leave_set', 3600, function () {
        return static::where('is_leave', true)
            ->where('is_active', true)
            ->pluck('short_code')->all();
    });
}
```

Returns `["AL", "BAL"]` currently. Used in `RotaResource` to set `is_leave: true/false` per row. Cached for 1 hour to avoid N+1 queries.

### 3.4 Backend files changed

| File | Purpose |
|---|---|
| `app/Http/Controllers/Api/App/RotaController.php` | Dispatch on `mode` |
| `app/Services/Rota/AppRotaService.php` | New `getAuthUserRollingWindowRotas` method |
| `app/Http/Resources/RotaResource.php` | Adds `is_leave` flag |
| `app/Models/ShortCode.php` | `leaveCodes()` helper |
| `public/deploy-v2.php` | Fixed auto-deploy webhook (correct repo + path + token + cache clear) |

**Deployed via:** GitHub push → webhook → `https://mobile-api.gbspares.com/deploy-v2.php`

---

## 4. Mobile Logic (composeApp, Kotlin Multiplatform)

### 4.1 Data flow
```
HomeViewModel.loadHomeShifts()
   └─ GetAuthUserRotaUseCase.getLastNDaysRota(35)
        └─ UserRepository.getAuthUserRotaLastNDays(35)
             └─ RemoteDataSource.getAuthUserRotaLastNDays(35)
                  └─ POST /rota/month/auth-user  mode=custom days=35
   ↓ result (List<Rota>, already sorted ASC by backend)
   └─ uiState.monthlyRotas = result.data  (NO client-side sort)
```

**Important:** `HomeViewModel` does NOT re-sort. The backend's ASC order is preserved.

### 4.2 Auto-scroll to today

```kotlin
LaunchedEffect(monthlyShifts) {
    if (monthlyShifts.isNotEmpty() && !hasScrolledToToday) {
        val todayIndex = monthlyShifts.indexOfFirst { it.fullDate == today }
        listState.scrollToItem(if (todayIndex >= 0) todayIndex else 0)
        hasScrolledToToday = true
    }
}
```

Today becomes the first visible row. User can scroll **up** to see past or **down** to see future.

### 4.3 Shift categorization (`UpcomingShiftCard::categorize`)

Priority order (first match wins):

| # | Condition | Category |
|---|---|---|
| 1 | `shift.isLeave == true` | LEAVE |
| 2 | `shift.shiftStatus == "off"` OR `shortCode == "OFF"` | OFF |
| 3 | `shortCode` starts with `N` OR `shiftType` contains "night"/"evening" | NIGHT |
| 4 | else | DAY |

Floor is NOT a category — other-floor shifts use the same day/night color.

### 4.4 Row layout

```
[Date badge 50x50] gap 12dp [Detail container, flex 1, 50dp]
                            ┌─────────────────────────────────┐
                            │ Title text (16sp)               │
                            │ Subtitle (10sp) ← only DAY/NIGHT│
                            └─────────────────────────────────┘
```

**Title text:**
- DAY/NIGHT: `"08:00 - 17:00 Day • 3F"` (time + label + floor)
- OFF: `"No Shift"` (no location, no designation)
- LEAVE (AL): `"✈️ Annual Leave"`
- LEAVE (BAL): `"🎂 Birthday Leave"`
- LEAVE (other): just the shift label

### 4.5 Color system

**Date badge (left, 50x50dp):**

| Category | Bg | Text |
|---|---|---|
| DAY | `#FFFFFF` white | dark |
| NIGHT | `#000000` black | white |
| OFF | `#E9EFF1` grey | dark |
| LEAVE | `#B8EAFF` blue | dark |
| **TODAY (any category)** | `#C8E6C9` light green (with 2dp border) | dark |

**Detail row (right):**

| Status | Bg | Text |
|---|---|---|
| Normal day | `#CFE6F1` light blue | dark |
| Night shift | `#000000` black | white |
| Day off | `#E9EFF1` grey | dark |
| Leave | `#B8EAFF` blue | dark |
| Today | `#C8E6C9` light green | dark, **bold** |

Defined as private vals at top of `UpcomingShiftCard.kt` (lines 32–36).

### 4.6 Mobile files changed

| File | Purpose |
|---|---|
| `data/model/RotaResponse.kt` | `RotaDto.isLeave` field |
| `domain/model/Rota.kt` | `Rota.isLeave` field |
| `data/mapper/RotaMapper.kt` | maps `isLeave` through |
| `presentation/screen/home/HomeViewModel.kt` | `getLastNDaysRota(35)`, no sort |
| `presentation/screen/home/HomeScreen.kt` | Layout (teal header, Active card, My Shifts card) |
| `presentation/component/UpcomingShiftCard.kt` | Category logic + colors + emoji |
| `presentation/component/MainHeaderContent.kt` | Greeting only (no date) |
| `presentation/component/CurrentShiftContent.kt` | Active Shift Card (map, button) |

---

## 5. Complete Color Reference

### 5.1 Theme tokens (from `presentation/theme/Color.kt`)

| Token | Hex | Where used on Home |
|---|---|---|
| `primaryLight` | `#007B99` (teal) | Teal header banner bg, "My Shifts" title, "see all" link, button bg, date badge border, locate-me icon, bottom-nav top border |
| `onPrimaryLight` | `#FFFFFF` | Greeting text, notification bell, button text |
| `primaryContainerLight` | `#B8EAFF` | LEAVE row date badge (theme token reused as `BgLeave`) |
| `onPrimaryContainerLight` | `#001F28` | (not used on Home directly) |
| `secondaryLight` | `#4C616B` | (not used on Home directly) |
| `secondaryContainerLight` | `#CFE6F1` | Detail row background (Figma's `FigmaShiftRowBg`) |
| `tertiaryContainerLight` | `#9CEFFA` | (legacy — not used on Home now) |
| `errorLight` | `#BA1A1A` | Stop Shift button bg (destructive state) |
| `surfaceLight` / `backgroundLight` | `#F5FAFE` | (not the screen bg here — screen uses pure white) |
| `surfaceContainerLight` | `#E9EFF1` | OFF row date badge (`BgOff`) |
| `onSurfaceLight` | `#171C1F` | (not used directly — Home uses pure `#000000` for text) |
| `onSurfaceVariantLight` | `#70787C` | Rota period subtitle (e.g. "3 May 2026 – 26 May 2026") |

### 5.2 Custom (non-theme) colors used on Home

| Constant | Hex | Where defined | Where used |
|---|---|---|---|
| `FigmaCardBackground` | `#F2FCFF` | `HomeScreen.kt` line 36 | Active Shift Card bg, My Shifts Card bg |
| `FigmaSelectedBadgeBg` | `#2B3133` | (was in `UpcomingShiftCard.kt`, now replaced by `BgTodayHighlight`) | Was today's badge bg, now unused |
| `FigmaOnPrimaryContainer` | `#004D61` | `CurrentShiftContent.kt` | Time text + role text on Active Shift Card |
| `FigmaError` | `#BA1A1A` | `CurrentShiftContent.kt` | Stop Shift button (matches `errorLight`) |
| `FigmaPrimary` | `#007B99` | `UpcomingShiftCard.kt` line 26 | Date badge border (1dp normal, 2dp for today) |
| `FigmaTextDark` | `#000000` | `UpcomingShiftCard.kt` line 29 | All row text (title + subtitle + arrow icon) |
| `FigmaHandoverBg` | `#9DF0FB` | `UpcomingShiftCard.kt` line 27 | Pending Handover badge bg |
| `FigmaHandoverText` | `#004F56` | `UpcomingShiftCard.kt` line 28 | Pending Handover badge text |
| `BgDay` | `#FFFFFF` (white) | `UpcomingShiftCard.kt` line 32 | Day shift date badge bg |
| `BgNight` | `#000000` (black) | `UpcomingShiftCard.kt` line 33 | Night shift date badge bg + detail row bg (NIGHT only) |
| `BgOff` | `#E9EFF1` (grey) | `UpcomingShiftCard.kt` line 34 | OFF date badge + detail row bg (OFF only) |
| `BgLeave` | `#B8EAFF` (light blue) | `UpcomingShiftCard.kt` line 35 | LEAVE date badge + detail row bg (LEAVE only) |
| `BgTodayHighlight` | `#C8E6C9` (light green) | `UpcomingShiftCard.kt` line 36 | Today's date badge bg AND detail row bg |

### 5.3 Section-by-section color map

#### Teal Header Banner
- Background: `MaterialTheme.colorScheme.primary` = `#007B99`
- Greeting text: `Color.White`
- Bell icon: `Color.White`
- Banner corner radius (bottom): 24dp

#### Active Shift Card
- Container bg: `#F2FCFF` (`FigmaCardBackground`)
- Container corner: 24dp
- Container elevation: 2dp
- Map placeholder bg: (whatever MapboxView renders)
- Time text color: `#004D61` (`FigmaOnPrimaryContainer`), Bold 16sp
- Role text color: `#004D61`, Regular 16sp
- Start Shift button: bg `#007B99`, text white, 42dp tall, 16dp corner
- Stop Shift button: bg `#BA1A1A`, text white (destructive)
- Locate-me circular button: `Color.White.copy(alpha=0.9f)` bg, `#007B99` icon

#### My Shifts Card
- Container bg: `#F2FCFF` (`FigmaCardBackground`)
- Container corner: 24dp
- Container elevation: 2dp
- "My Shifts" title: `#007B99`, Bold 20sp, line-height 28sp
- Period subtitle: `onSurfaceVariant` `#70787C`, 12sp
- "see all" link: `#007B99`, 12sp, letter-spacing 0.4sp

#### Shift Row — Date badge (LEFT 50x50dp)
| Category | Badge bg | Badge text | Border |
|---|---|---|---|
| Today (any) | `#C8E6C9` light green | `#000000` dark | `#007B99` 2dp |
| Day | `#FFFFFF` white | `#000000` dark | `#007B99` 1dp |
| Night | `#000000` black | `#FFFFFF` white | `#007B99` 1dp |
| Off | `#E9EFF1` grey | `#000000` dark | `#007B99` 1dp |
| Leave | `#B8EAFF` blue | `#000000` dark | `#007B99` 1dp |

#### Shift Row — Detail container (RIGHT, flex 1, 50dp)
| Category | Row bg | Text color |
|---|---|---|
| Today (any) | `#C8E6C9` light green | `#000000` dark, **Bold** title |
| Day (not today) | `#CFE6F1` light blue (Figma default) | `#000000` dark |
| Night (not today) | `#000000` black | `#FFFFFF` white |
| Off (not today) | `#E9EFF1` grey | `#000000` dark |
| Leave (not today) | `#B8EAFF` blue | `#000000` dark |

#### Pending Handover badge (overlay inside detail row)
- Bg: `#9DF0FB`
- Text: `#004F56`, Medium 10sp, line-height 12sp
- Corner: 6dp

#### Right arrow icon (end of detail row)
- Tint: `#000000` dark
- Size: 20dp

#### Shimmer placeholder (during loading)
- Shimmer band: gradient of `Color.White.copy(0.3f) → 0.1f → 0.3f` on teal header
- Shimmer rows in My Shifts: 50x50 badge + flex row, 12dp corner, default shimmer colors

### 5.4 Quick visual legend

```
DAY shift     [white badge] [light-blue row]    ← lightest combo
NIGHT shift   [black badge] [black row]         ← darkest, white text
OFF (no shift)[grey badge]  [grey row]          ← neutral, no info
LEAVE         [blue badge]  [blue row]          ← stands out, emoji prefix
TODAY (any)   [GREEN badge 2dp border] [GREEN row, bold text]  ← always wins
```

---

## 6. "How do I change X?" Cheat Sheet

| To change… | Edit… | What |
|---|---|---|
| Number of shifts shown (35 → 50) | `HomeViewModel.kt` line ~57 | `getLastNDaysRota(35)` and backend `days` cap |
| Day shift color | `UpcomingShiftCard.kt` line 32 | `BgDay = Color(0xFFFFFFFF)` |
| Night shift color | `UpcomingShiftCard.kt` line 33 | `BgNight = Color(0xFF000000)` |
| Day off color | `UpcomingShiftCard.kt` line 34 | `BgOff = Color(0xFFE9EFF1)` |
| Leave color | `UpcomingShiftCard.kt` line 35 | `BgLeave = Color(0xFFB8EAFF)` |
| Today highlight color | `UpcomingShiftCard.kt` line 36 | `BgTodayHighlight = Color(0xFFC8E6C9)` |
| Add a new emoji for a leave type | `UpcomingShiftCard.kt::titleText` | Add case in `when (shift.shortCode.uppercase())` |
| Add a new leave short_code | Admin panel → short_codes table | Set `is_leave=true` (auto-detected next request, 1-hour cache) |
| Backend window algorithm | mobile-api `AppRotaService::getAuthUserRollingWindowRotas` | The future-then-pad-with-past logic |
| Auto-deploy on push | webhook → `https://mobile-api.gbspares.com/deploy-v2.php` | Already wired |

---

## 7. Known State (as of 2026-05-26)

- ✅ Backend deployed on production via auto-deploy
- ✅ Mobile code updated to match
- ✅ API returns 35 rotas in ASC order with `today_index`
- ✅ Auto-deploy is now WORKING (was broken — wrong repo + path + expired token)
- ⚠️ Backend PHP files are NOT yet pushed to GitHub `wfm-suite/mobile-api`. Until pushed, a manual deploy from someone else could overwrite them. Push when ready.

---

## 8. Quick API Test (curl)

```bash
# Login
TOKEN=$(curl -s -X POST 'https://mobile-api.gbspares.com/api/app/login' \
  -d 'email=osikur@gmail.com&password=123456' \
  | python3 -c "import json,sys;print(json.load(sys.stdin)['data']['access_token'])")

# My Shifts (35-day rolling window)
curl -s -X POST 'https://mobile-api.gbspares.com/api/app/rota/month/auth-user' \
  -H "Authorization: Bearer $TOKEN" \
  -d 'mode=custom&days=35'
```

Expected: 35 rotas, today_index=N, sorted ASC by date.

---

**File saved at:** `/Users/osikurrahman/Downloads/WorkLog-osikur/HOMEPAGE_NOTES.md`
