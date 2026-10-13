# Modül Entegrasyon Rehberi

## Hedef

Reader, Media, Dialogue ve sonraki özellikleri tek APK içinde Android library modülleri olarak bağlamak; shell ile özellikler arasındaki bağımlılığı minimumda tutmak.

Bu, yalnız **tek APK içinde gömülü teslimat seçilirse** uygulanacak en sade Gradle yaklaşımıdır. Aile standardının bağımsız ürün güncelleme hedefi açısından normal Android library kodu shell APK’sı değişmeden güncellenemez. Ayrı paket + ilan edilmiş API/IPC/deep link/provider veya dynamic feature seçeneklerinden hangisinin kullanılacağı henüz açık tasarım kararıdır; bu belge o kararı verilmiş saymaz.

## Önerilen en sade yaklaşım

Her özellik için bir Android library modülü oluşturun; örneğin `:features:reader`. Modülde `com.android.library` kullanın, `applicationId`, launcher Activity veya ayrı APK tanımlamayın. Compose UI gerekiyorsa Kotlin Compose plugin’i ve yalnızca o modülün kullandığı bağımlılıkları ekleyin.

Shell’in feature’dan ihtiyaç duyduğu ilk yüzey tek bir giriş composable’ı olabilir:

```kotlin
@Composable
fun ReaderEntry(onBack: () -> Unit)
```

Ardından:

1. `settings.gradle.kts` içine `include(":features:reader")` ekleyin.
2. `:app` içine `implementation(project(":features:reader"))` ekleyin.
3. Reader kartının placeholder rotasını `ReaderEntry` ile değiştirin.
4. Sistem ve top-app-bar Back sözleşmesini shell sahibi olarak koruyun.
5. Feature unit/UI testlerini kendi modülünde, shell yönlendirme testini `:app` içinde tutun.

## Bağımlılık kuralları

- Yön `:app -> :features:<name>` olmalıdır; feature modülü `:app`e bağımlı olmamalıdır.
- Feature’lar birbirine doğrudan bağımlı olmamalıdır. Gerçekten ortak kod doğarsa küçük bir `:core:<purpose>` modülü ayrıca gerekçelendirilmelidir.
- P01 aşamasında ortak framework, service locator, event bus veya genel “plugin” soyutlaması eklemeyin.
- Görünen feature metinleri ve varlıkları feature modülünde; suite adı, shell navigasyonu ve global tema shell’de kalmalıdır.
- Sürüm kodu/adı yalnızca application modülüne aittir ve kök `gradle.properties` kaynağından gelmelidir.

## Dialogue applicationId doğrulaması

Shell applicationId’si `com.theemrock.smooth` olarak sabittir. Salt okunur aile standardı Dialogue için `com.theemrock.smooth.dialogue` kimliğini belirtir; bu karara göre doğrudan çakışma beklenmez. Dialogue projesine erişim yasak olduğundan gerçek manifest/imza durumu bu denetimde bağımsız doğrulanmadı. Eğer eski bir build hâlâ shell kimliğini kullanıyorsa aynı cihazda birlikte kurulum veya imza uyumluluğu sorunu doğabilir; ilk entegrasyon öncesi yetkili ayrı doğrulama gerekir.

## Tek APK kuralı

Feature modülleri `com.android.library` kalmalı, kendi launcher intent-filter’larını ve applicationId’lerini taşımamalıdır. Dağıtılan tek artifact `:app` APK/AAB olmalıdır.
