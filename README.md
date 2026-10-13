# SMOOTH

SMOOTH, gelecekteki Suite modülleri için tek APK üreten Android shell uygulamasıdır. P01 sürümünde Home ekranı Agent, Dialogue, Reader, Wiki, Nav, Media, Reserved ve Settings kartlarını iki sütunda gösterir. İlk yedi kart yalnızca uygulama içi yer tutucu ekranlara gider; Settings gerçek paket sürümünü ve henüz uygulanmamış güncelleme kontrolü açıklamasını gösterir.

## Durum

- Application ID: `com.theemrock.smooth`
- Uygulama adı: `SMOOTH`
- Mevcut sürüm: `v0.0.01.001` (`1001`)
- Checkpoint: tamamlandı; 2 JVM ve 10 gerçek cihaz instrumented testi PASS.
- Teknoloji: Kotlin, Jetpack Compose, Material 3, minSdk 33, compile/targetSdk 35.
- Harici APK başlatma, gerçek feature modülü veya ağ tabanlı güncelleme kontrolü yoktur.

## Komut satırından derleme

PowerShell:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='ANDROID_SDK_PATH'
.\gradlew.bat clean :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
```

Debug APK:

`C:\Projects\Smooth\app\build\outputs\apk\debug\app-debug.apk`

Instrumented testleri çalıştırmak, uygulama ve test APK’sını hedef cihaza/emülatöre kurar:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Bu denetimde 10 instrumented test Samsung Galaxy S24 Ultra üzerinde çalıştırıldı ve tamamı geçti. Final debug APK cihazda kurulu bırakıldı. Ayrıntılar `docs/TEST_REPORT.md` içindedir.

## Sürümleme

Tek sürüm kaynağı `gradle.properties` içindeki `smoothVersionName` ve `smoothVersionCode` değerleridir. `app/build.gradle.kts` bu değerleri okur; Settings ekranı `BuildConfig.VERSION_NAME` gösterir.

## Kapsam sınırları

Launcher ikonu yer tutucudur; nihai logo, ikon, font ve marka kararı değildir. Reader, Media ve Dialogue kodları bu projeye taşınmamıştır. Modülerleşme önerisi `docs/MODULE_INTEGRATION.md` içindedir.
