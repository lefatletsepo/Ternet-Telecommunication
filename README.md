# Ternet Telecommunications Mobile Core App
### Modern M-Pesa Lesotho Rebrand & High-Fidelity Jetpack Compose Replica

Welcome to the architectural repository for **Ternet Telecommunications**, a luxury re-branded and heavily customized high-fidelity clone of the **M-Pesa Lesotho** mobile banking and digital wallet application.

This repository features the complete architectural structure, design system, core UI components, and state-management models. It implements an advanced **"Liquid Glass" / Glassmorphic UI layout** utilizing the finest declarative patterns in **Kotlin and Jetpack Compose**.

---

## 🎨 Brand Identity & Design System

The traditional red branding of M-Pesa is fully replaced with an organic, sleek dark-mode canvas accented with vibrant emerald greens and neon-liquid mints. 

### 🧊 Modern "Liquid Glass" Visual Language
The interface simulates physical frosted glass plates overlaying deep, organic fluids. It relies on four visual principles:
1. **Refraction & Depth:** Organic backing canvas (`Canvas` inside `LoginScreen` and `DashboardScreen`) drawing overlapping radial-gradient circles representing fluid blobs. An infinite animation transitions their coordinates, causing the colors to blend organically like thick liquid.
2. **Backdrop Blur:** OS-level background blur applied natively on Android 12+ (API 31+) using `Modifier.blur(...)` combined with translucent white alphas (`0x1EFFFFFF`) to simulate frosted glass.
3. **Reflective Specularity:** Custom border stroke gradients starting with high-specular white (`0x66FFFFFF`) at the top-left (light source) and fading to a faint shade (`0x14FFFFFF`) at the bottom-right.
4. **Active Luminescence:** Important interactive components emit a soft, mint-glowing outer drop-shadow (`Paint().asFrameworkPaint().setShadowLayer`), blending the card with the backing liquid canvas.

---

## 🏗️ Clean MVVM Architecture

The codebase strictly adheres to the standard Clean Architecture guidelines coupled with **Model-View-ViewModel (MVVM)** representation. This segregates domain business logic from the view layout, ensuring scalability and unit-testability:

```text
/ternet-telecom/
├── README.md                       <-- Tech Spec & Architectural Blueprint
└── app/
    └── src/
        └── main/
            └── java/
                └── com/
                    └── ternet/
                        └── telecom/
                            ├── MainActivity.kt        <-- Navigation Controller & Surface Root
                            ├── domain/
                            │   └── model/
                            │       ├── Transaction.kt <-- Transaction Entity (Lesotho Context)
                            │       └── WalletState.kt   <-- Core Balance, Caps & Limits
                            ├── viewmodel/
                            │   ├── LoginViewModel.kt
                            │   ├── DashboardViewModel.kt
                            │   ├── SendMoneyViewModel.kt
                            │   └── PayMerchantViewModel.kt
                            └── ui/
                                ├── theme/
                                │   ├── Color.kt       <-- Forest Green, Specular & Liquid Palettes
                                │   ├── Theme.kt       <-- Custom TernetTheme helper gradients
                                │   └── Type.kt        <-- Strict Typographic scale
                                ├── components/
                                │   ├── GlassmorphicCard.kt    <-- Reusable Composable & Modifier
                                │   └── GlassmorphicKeypad.kt  <-- Secure tactile PIN pad
                                └── screens/
                                    ├── LoginScreen.kt        <-- Animated PIN Login screen
                                    ├── DashboardScreen.kt    <-- Home Dashboard & Recent Ledger
                                    ├── SendMoneyScreen.kt    <-- Dynamic transfer & tiered fees
                                    └── PayMerchantScreen.kt  <-- Lipha Till pay & camera viewport
```

---

## 🇱🇸 Lesotho Localization & Regional Specs

We have carefully maintained high-fidelity compliance with Lesotho's regional mobile money realities:
* **The Loti (plural Maloti) Currency:** Balance figures and transactions are represented with the currency symbol **M** (Lesotho Maloti) or **LSL**, adhering to regional banking practices.
* **Country Prefix Integration:** Phone input fields are prepended with **+266** (Lesotho's international calling code).
* **Lipha Merchant Payments:** Replicates M-Pesa's **"Lipha"** merchant system. The Pay Merchant screen accepts 4-to-6-digit Lipha till numbers (e.g., Shoprite Pioneer Mall `#4109`) and offers scanning guidelines.
* **Tiered M-Pesa Lesotho Fee Replication:** Transaction fees are dynamically calculated in real-time within `SendMoneyViewModel` following classic regional transaction brackets:
  * Up to M 50.00 ➔ **M 1.50**
  * M 50.01 - M 100.00 ➔ **M 2.50**
  * M 100.01 - M 500.00 ➔ **M 7.00**
  * M 500.01 - M 1000.00 ➔ **M 12.00**
  * M 1000.01 - M 2500.00 ➔ **M 22.00**
  * Above M 2500.00 ➔ **M 35.00**
  * *Note: Merchant (Lipha) payments are configured as 100% free of network charges.*

---

## 💻 Tech Stack & Requirements

* **Development Language:** Kotlin 1.9+ (Coroutines + Flow/StateFlow representation)
* **UI Framework:** Jetpack Compose (Material 3 standard)
* **Minimum Android SDK:** API 21 (Lollipop)
* **Target Android SDK:** API 34+
* **Core Dependency Requirements (`build.gradle.kts` setup):**
  ```kotlin
  dependencies {
      // Core Compose
      implementation("androidx.compose.ui:ui:1.6.0")
      implementation("androidx.compose.material3:material3:1.2.0")
      implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
      
      // Navigation & Lifecycle ViewModels
      implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
      implementation("androidx.activity:activity-compose:1.8.2")
  }
  ```

---

## 🔒 Security Standards Implemented

1. **Automatic PIN Release:** Transitioning away from the `DASHBOARD` (e.g., logging out) automatically calls `onClearPress()` inside `LoginViewModel` to purge the active PIN string from memory.
2. **Secure Obfuscation:** The primary wallet card relies on standard character obfuscation (`M ••••••••`) utilizing discrete state transitions. This prevents shoulder-surfing in public environments.
3. **Local Biometric Emulation:** Emulates biometric bypasses through standard crypto-handshake protocols.

---

## 🛠️ Verification & Execution

To test the high-fidelity flows in emulator/device:
1. Load `/ternet-telecom` into **Android Studio**.
2. Run the application on an emulator running **Android 12+ (API 31+)** to experience the native multi-pass blur pipelines.
3. On the Login Screen, enter the Lesotho Demo Passcode **`2660`** or the universal demo bypass **`1234`** or tap the **Fingerprint Icon** to execute a simulated secure biometric log-in.
4. Try clicking **Send Money** or **Lipha (Pay)** on the dashboard to test input validation, tiered fees, and mock receipt success.
