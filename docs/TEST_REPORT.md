# P01 Test ve Denetim Raporu

Tarih: 2026-10-13
Hedef: `C:\Projects\Smooth`
Sonuç: **P01-01 checkpoint tamamlandı.**

## Son sürüm ve artifact

- applicationId: `com.theemrock.smooth`
- versionName: `v0.0.01.001`
- versionCode: `1001`
- Debug APK: `C:\Projects\Smooth\app\build\outputs\apk\debug\app-debug.apk`
- Varlık: PASS
- Boyut: 25.069.455 byte
- SHA-256: `6A05C2DB06234C00220C5C1B37B0291FE47338F5E835A7D1154F45FEC456A772`

`aapt2 dump badging` paket adını, sürümü, minSdk 33, targetSdk 35 ve `SMOOTH` etiketini doğruladı.

## Otomatik test sayıları

- PASS: 12 test (2 JVM + 10 instrumented Compose/UI)
- FAIL: 0
- NOT RUN: 0

Hedef cihaz: Samsung Galaxy S24 Ultra (`SM-S928B`), seri `R5CXA0LXZRB`, Android 16, API 36, güvenlik yaması 2026-09-05. Tek yetkili cihaz açıkça seçildi. Final APK `adb install -r` ile veri temizlemeden kuruldu ve cihazda bırakıldı.

### Instrumented test sonuçları

| Test | Sonuç |
|---|---|
| `allCardsRemainReachableWithoutTextClippingOnSmallScreenAtLargeFontScale` | PASS |
| `lightThemeUsesRequiredCoreColors` | PASS |
| `darkThemeUsesRequiredCoreColors` | PASS |
| `defaultAndTurkishResourcesContainExpectedVisibleCopy` | PASS |
| `homeShowsEightCardsInRequiredTwoColumnOrder` | PASS |
| `systemBackReturnsFromDetailToHomeWithoutClosingActivity` | PASS |
| `recreationRetainsCurrentDestination` | PASS |
| `settingsShowsRealStateAndSurvivesRecreationThenSystemBack` | PASS |
| `rotationRetainsCurrentDestination` | PASS |
| `everyCardOpensItsExpectedScreenAndTopBarBackReturnsHome` | PASS |

## Gerçek komutlar ve sonuçlar

Ortam değişkenleri kullanılan Gradle komutlarında:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='ANDROID_SDK_PATH'
```

| Komut | Sonuç | Kanıt |
|---|---|---|
| `.\gradlew.bat :app:compileDebugKotlin :app:compileDebugAndroidTestKotlin :app:testDebugUnitTest --stacktrace` | PASS | Üretim/test Kotlin derlendi; 2/2 unit test geçti |
| `.\gradlew.bat :app:testDebugUnitTest --stacktrace` | PASS | 2 test, 0 failure, 0 error |
| `.\gradlew.bat :app:assembleDebugAndroidTest --stacktrace` | PASS | Test APK derlendi; testler çalıştırılmadı |
| `.\gradlew.bat :app:dependencies --configuration debugRuntimeClasspath --console=plain` | PASS | Dependency graph tek sürümlere çözüldü |
| `.\gradlew.bat :app:assembleRelease --stacktrace` | PASS | Release variant derlendi; signing yapılandırması yok |
| `.\gradlew.bat clean :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug --stacktrace` | PASS | 81 görev çalıştı, clean build başarılı |
| `.\gradlew.bat :app:assembleDebug :app:lintDebug --stacktrace` | PASS | Son kaynak temizliği sonrası build/lint başarılı |
| `adb devices -l` | PASS | Tek hedef: `R5CXA0LXZRB`, `SM-S928B`, authorized |
| `.\gradlew.bat :app:connectedDebugAndroidTest --stacktrace` | PASS | Başlangıç sürümünde 10/10 cihaz testi |
| `.\gradlew.bat clean :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug :app:connectedDebugAndroidTest --stacktrace` | PASS | Checkpoint sürümünde 82 görev; 2/2 JVM ve 10/10 cihaz testi |
| `adb -s R5CXA0LXZRB install -r app-debug.apk` | PASS | Final APK kuruldu; mevcut veri için ayrı temizleme yapılmadı |

İlk kurtarma sırasında gözlenen ve giderilen build hataları:

1. Gradle başlamadan `JAVA_HOME` eksikti — komut ortamında Android Studio JBR kullanıldı.
2. Gradle başlamadan Android SDK yolu eksikti — komut ortamında `ANDROID_HOME` kullanıldı.
3. `:app:processDebugResources`, manifestte var olmayan `mipmap/ic_launcher*` referanslarında duruyordu — yer tutucu vector ile değiştirildi.
4. `:app:compileDebugKotlin`, `MainActivity.kt` içindeki bozuk `\"` kaçışlarında duruyordu — geçerli Kotlin stringlerine düzeltildi.
5. `gradle.properties` başındaki UTF-8 BOM nedeniyle JVM argümanlarının okunmaması — dosya BOM’suz yazıldı; `-Xmx2048m` ve `MaxMetaspaceSize=1024m` etkinleşti.

## P01 kabul matrisi

| Kriter | Durum | Kanıt / sınırlama |
|---|---|---|
| applicationId ve uygulama adı | PASS | `aapt2`: `com.theemrock.smooth`, `SMOOTH` |
| Kotlin + Compose + Material 3; CLI build | PASS | Clean `assembleDebug` |
| İki sütunda zorunlu sekizli sıra | PASS | Unit sıra testi ve gerçek cihaz Compose yerleşim testi |
| Her kartın doğru ekrana açılması ve geri dönüş | PASS | 8 kart, top-bar Back ve system Back cihazda geçti |
| Settings adı, gerçek sürüm, dürüst update durumu | PASS | `BuildConfig.VERSION_NAME`, dialog ve recreation cihazda geçti |
| Zorunlu dark/light renkler | PASS | İki theme testi cihazda geçti |
| İngilizce ve Türkçe kaynaklar | PASS | Locale testi cihazda geçti |
| Telefon, küçük ekran, büyük font | PASS | 320×480dp / fontScale 2.0; 8 kart erişim/taşma testi geçti |
| Activity recreation ve rotation | PASS | Recreation ve doğrulanan portrait→landscape testi geçti |
| Merkezi checkpoint sürümü | PASS | `gradle.properties` ve APK: `1001/v0.0.01.001` |

Tüm zorunlu testler geçtiği için `v0.0.01.001` / `1001` checkpoint’i uygulandı ve final APK yeniden üretildi.

## Lint, güvenlik ve bakım

- Android Lint: PASS, 0 error / 11 warning.
- Kalan 11 uyarı: SDK hedef/güncellik (2), Gradle/AGP/Kotlin araç zinciri güncelliği (4), AndroidX/test library güncelliği (4), gelecekteki `dataExtractionRules` önerisi (1). P01’i etkileyen güvenlik, lifecycle veya erişilebilirlik hatası yok; kapsam gereği yükseltme yapılmadı.
- INTERNET veya tehlikeli runtime permission yok; servis yok.
- Uygulamanın yalnızca launcher `MainActivity` bileşeni exported. Debug test host ve AndroidX provider exported değil; profile installer receiver `android.permission.DUMP` ile korunuyor.
- APK arşivinde `.env`, `local.properties`, keystore/private-key, kullanıcı yolu veya kişisel ad izi bulunmadı.
- Kaynak ağacında secret biçimli değer bulunmadı.
- `.gitignore` yerel SDK, Gradle/IDE, build ve heap-dump çıktılarını dışlıyor; denetimde oluşan iki heap dump silindi.
- Kullanılmayan doğrudan `core-ktx`, `lifecycle-runtime-ktx`, preview/debug tooling bağımlılıkları ve var olmayan ProGuard dosyası referansı kaldırıldı.

## Açık teknik riskler

1. Aile standardı Dialogue kimliğini `com.theemrock.smooth.dialogue` olarak tanımlar; çakışma beklenmez. Dialogue projesine erişim yasak olduğundan gerçek manifest/imza durumu bağımsız doğrulanmadı.
2. `targetSdk` ve `compileSdk` 35; SDK 36 geçişi davranış/regresyon testi ister.
3. Yer tutucu launcher ikonu nihai marka varlığı değildir.
4. Release signing/minification politikası henüz ürün dağıtımı için tanımlı değildir.

## P02 için ilk beş iş

1. P01’in 12 testini CI/izinli cihaz regresyon kapısı olarak kalıcılaştır.
2. compile/target SDK 36 ve Gradle/AGP/Kotlin/AndroidX yükseltmesini ayrı değişiklikte test et.
3. Nihai logo, adaptive icon, font ve erişilebilirlik kararlarını tamamla.
4. Entegrasyon topolojisini karara bağla; library yaklaşımının bağımsız güncelleme sağlamadığını dikkate al.
5. Release signing, AAB, CI clean-build ve artifact bütünlük kontrollerini tanımla.
