# P01 Mimarisi

## Genel yapı

Proje tek Gradle modülü (`:app`) ve tek Android application üretir. `MainActivity`, Compose kökünü açar; `SmoothApp` Home, Settings ve placeholder ekranları arasında uygulama içi durumla geçiş yapar. Navigation Compose, DI, event bus, servis veya ağ katmanı yoktur.

## Bileşenler

- `MainActivity.kt`: sekizli modül kataloğu, shell rotaları, Home grid, placeholder ve Settings UI.
- `ui/theme/Color.kt` ve `Theme.kt`: zorunlu dark/light renk şemaları.
- `res/values` ve `res/values-tr`: varsayılan İngilizce ve Türkçe görünen metinler.
- `gradle.properties`: `versionName` ve `versionCode` için tek merkezi kaynak.

## Navigasyon ve lifecycle

Aktif rota ile seçili modül kimliği `rememberSaveable` kullanır. Bu seçim Activity recreation ve configuration change sırasında saved-instance state üzerinden korunacak şekilde tasarlanmıştır. Detay ekranındayken sistem Back ve top-app-bar Back, shell durumunu Home’a çevirir; Home’daki normal sistem Back davranışı Activity’ye bırakılır.

Rotalar yalnızca shell içindedir:

- `home`
- `settings`
- `placeholder/{moduleId}`

İlk yedi kart gerçek modül başlatmaz. Settings de shell ekranıdır.

## Güvenlik sınırı

Ana manifest INTERNET veya tehlikeli runtime permission istemez; servis tanımlamaz. Uygulamanın yalnızca launcher `MainActivity` bileşeni exported olarak açılır. AndroidX başlangıç provider’ı exported değildir; profile installer receiver’ı platform `DUMP` izniyle korunur. Debug varyantındaki `TestHostActivity` yalnızca izole Compose testleri içindir ve exported değildir; release varyantında yer almaz. Uygulama yedeklemesi kapalıdır.

## Bilinen sınırlar

- Yer tutucu launcher vector’ü nihai marka varlığı değildir.
- UI/lifecycle testleri derlendi fakat cihaz kurulumu yasağı nedeniyle çalıştırılmadı.
- `targetSdk`/`compileSdk` 35’tir; kurulu SDK 36 ve daha yeni bağımlılıklar için ayrı, kontrollü yükseltme gerekir.
- Bu klasör bir Git deposu değildir; denetim commit/tag üretmedi.
