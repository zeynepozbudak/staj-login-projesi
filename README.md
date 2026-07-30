# VBT Staj Projesi — Frontend (React + Vite)

## Proje Hakkında

Bu proje, VBT stajı kapsamında geliştirilen JWT tabanlı kimlik doğrulama sisteminin **React frontend**'idir. Kullanıcılar giriş yapabilir, kayıt olabilir ve dashboard sayfasına erişebilir. Backend API (Spring Boot, port 8080) ile iletişim kurar.

> **Not:** Projede iki ayrı frontend bulunmaktadır:
> - `frontend/` — İlk geliştirilen Vanilla HTML/JS versiyonu (Tailwind CDN + SweetAlert2)
> - `react-frontend/` — React ile yeniden yazılmış modern versiyon **(ana frontend)**

Bu README, **React frontend** (`react-frontend/`) için hazırlanmıştır. Vanilla frontend hakkında bilgi de aşağıda yer almaktadır.

---

## Kullanılan Teknolojiler

| Teknoloji | Versiyon | Açıklama |
|---|---|---|
| **React** | 19.2.7 | UI kütüphanesi |
| **Vite** | 8.1.1 | Build aracı ve geliştirme sunucusu |
| **React Router DOM** | 7.18.1 | Sayfa yönlendirme (client-side routing) |
| **Axios** | 1.18.1 | HTTP istemcisi (interceptor desteğiyle) |
| **React Hook Form** | 7.82.0 | Form yönetimi |
| **Zod** | 4.4.3 | Form validasyon şemaları |
| **@hookform/resolvers** | 5.4.0 | Zod + React Hook Form entegrasyonu |
| **Tailwind CSS** | 4.3.3 | Utility-first CSS framework |
| **PostCSS** | 8.5.19 | CSS post-işlemci |
| **Autoprefixer** | 10.5.4 | Otomatik tarayıcı prefix ekleme |
| **Playwright** | 1.61.1 | End-to-End (E2E) test framework'ü |
| **ESLint** | 10.6.0 | Kod kalite kontrolü |

## Proje Yapısı

```
react-frontend/
├── public/                        # Statik dosyalar
├── src/
│   ├── main.jsx                   # Uygulama giriş noktası (React root render)
│   ├── App.jsx                    # Route tanımları (BrowserRouter)
│   ├── App.css                    # Genel stiller
│   ├── index.css                  # Tailwind import
│   ├── api.js                     # Axios instance + interceptor'lar
│   ├── assets/
│   │   ├── hero.png               # Görsel
│   │   ├── react.svg              # React logosu
│   │   └── vite.svg               # Vite logosu
│   └── components/
│       ├── LoginForm.jsx          # Giriş sayfası bileşeni
│       ├── RegisterForm.jsx       # Kayıt sayfası bileşeni
│       └── Dashboard.jsx          # Dashboard sayfası bileşeni
├── tests/
│   └── example.spec.js            # Playwright E2E test
├── index.html                     # HTML şablonu
├── package.json                   # Bağımlılıklar ve scriptler
├── vite.config.js                 # Vite yapılandırması
├── tailwind.config.js             # Tailwind CSS yapılandırması
├── postcss.config.js              # PostCSS yapılandırması
├── eslint.config.js               # ESLint yapılandırması
└── playwright.config.js           # Playwright test yapılandırması
```

## Sayfalar ve Route'lar

| Route | Bileşen | Açıklama |
|---|---|---|
| `/` | — | Otomatik olarak `/login`'e yönlendirir |
| `/login` | `LoginForm.jsx` | Kullanıcı giriş sayfası |
| `/register` | `RegisterForm.jsx` | Kullanıcı kayıt sayfası |
| `/dashboard` | `Dashboard.jsx` | Giriş sonrası karşılama sayfası |

## Özellikler

### Giriş Sayfası (`LoginForm.jsx`)
- Email ve şifre ile giriş
- Zod ile client-side form validasyonu (email formatı, şifre min 6 karakter)
- Şifre göster/gizle toggle
- Başarılı girişte access token localStorage'a kaydedilir ve Dashboard'a yönlendirilir
- Hata durumlarında kullanıcıya mesaj gösterilir (sunucu kapalı, hatalı bilgi vb.)
- **429 Rate Limit** koruması: Çok fazla hatalı giriş denemesinde buton 60 saniye devre dışı kalır ve geri sayım gösterilir

### Kayıt Sayfası (`RegisterForm.jsx`)
- Ad, soyad, email ve şifre alanları
- Zod ile validasyon (ad/soyad min 2 karakter, email formatı, şifre min 6 karakter)
- Şifre göster/gizle toggle
- Başarılı kayıtta bilgi mesajı gösterilir
- Email kullanımdaysa hata mesajı gösterilir

### Dashboard Sayfası (`Dashboard.jsx`)
- Giriş yapmış kullanıcının adı ve soyadı API'den çekilir (`/users/me`)
- "Hoş geldiniz [AD SOYAD]!" mesajı gösterilir
- Çıkış yap butonu: localStorage'dan token silinir ve login sayfasına yönlendirilir
- Veri yüklenirken "Yükleniyor..." animasyonu gösterilir

### API Katmanı (`api.js`)
- Axios instance oluşturulur (`baseURL: http://localhost:8080`, `withCredentials: true`)
- **Request Interceptor:** Her istekte localStorage'daki access token otomatik olarak `Authorization` header'ına eklenir
- **Response Interceptor:** 401 hatası alındığında otomatik olarak `/auth/refresh` endpoint'ine istek atılır, yeni token alınır ve orijinal istek tekrarlanır. Refresh da başarısız olursa kullanıcı login sayfasına yönlendirilir.

## Port

| Servis | Port | Açıklama |
|---|---|---|
| **Vite Dev Server** | `5173` (veya `5174`) | React geliştirme sunucusu |

> Backend API'nin `http://localhost:8080` adresinde çalışıyor olması gerekir.

## Kurulum ve Çalıştırma

### 1. Ön Gereksinimler

- Node.js (v18 veya üzeri önerilir)
- npm

### 2. Bağımlılıkları Yükleme

```bash
cd react-frontend
npm install
```

### 3. Geliştirme Sunucusunu Başlatma

```bash
npm run dev
```

Tarayıcıda açın: `http://localhost:5173`

### 4. Üretim Build'i

```bash
npm run build
```

Build çıktısı `dist/` klasörüne oluşturulur.

### 5. Build Önizleme

```bash
npm run preview
```

### 6. E2E Testleri Çalıştırma

```bash
npx playwright test
```

## Backend ile İletişim

Frontend, aşağıdaki backend endpoint'lerini kullanır:

| Kullanıldığı Yer | HTTP Metodu | Endpoint | Açıklama |
|---|---|---|---|
| LoginForm | `POST` | `/auth/login` | Kullanıcı girişi, access token alır |
| RegisterForm | `POST` | `/auth/register` | Yeni kullanıcı kaydı |
| api.js (interceptor) | `POST` | `/auth/refresh` | Token yenileme (cookie ile) |
| Dashboard | `GET` | `/users/me` | Kullanıcı bilgilerini çeker |

## Test Kapsamı

### E2E Test (Playwright)
- **Başarılı giriş senaryosu:** Login sayfasına gider, geçerli email/şifre girer, butona tıklar, "Giriş başarılı" mesajını doğrular.

---

## Vanilla Frontend (`frontend/`) — Ek Bilgi

İlk geliştirme aşamasında oluşturulan, React öncesi basit HTML/JS versiyonudur.

### Kullanılan Teknolojiler
- HTML5
- Vanilla JavaScript (ES6+)
- Tailwind CSS (CDN)
- Font Awesome (ikonlar)
- SweetAlert2 (bildirim popup'ları)

### Dosya Yapısı
```
frontend/
├── index.html        # Giriş sayfası
├── register.html     # Kayıt sayfası
├── dashboard.html    # Dashboard sayfası
└── main.js           # Tüm JavaScript mantığı (authFetch, login, register, logout)
```

### Özellikler
- Giriş, kayıt ve çıkış işlevleri
- Access token memory'de (JavaScript değişkeninde) saklanır
- Refresh token yenileme (`authFetch` ile otomatik 401 yakalama)
- Şifre göster/gizle toggle
- SweetAlert2 ile şık bildirimler

---

## Ekran Görüntüleri İçin Önerilen Yerler

> Aşağıdaki ekran görüntüleri README'ye eklenmelidir.

1. **Giriş Sayfası** — `http://localhost:5173/login` adresinin tam ekran görüntüsü. Sol tarafta mavi alan ve illüstrasyon, sağ tarafta giriş formu görünmelidir.
2. **Giriş — Validasyon Hataları** — Email ve şifre alanlarını boş bırakıp submit ettiğinizde görünen Zod validasyon mesajları (kırmızı yazılar).
3. **Giriş — Başarılı Giriş Mesajı** — Doğru bilgilerle giriş yapıldığında görünen yeşil "Giriş başarılı! Sisteme yönlendiriliyorsunuz..." mesajı.
4. **Giriş — Hatalı Bilgi Mesajı** — Yanlış şifre girildiğinde görünen kırmızı hata mesajı.
5. **Giriş — Rate Limit Kilidi** — 5'ten fazla hatalı giriş denemesinden sonra butonun devre dışı kalması ve "Lütfen 60 saniye bekleyin" geri sayımı.
6. **Kayıt Sayfası** — `http://localhost:5173/register` adresinin tam ekran görüntüsü. Ad, soyad, email, şifre alanları görünmelidir.
7. **Kayıt — Başarılı Kayıt Mesajı** — Başarılı kayıt sonrası görünen yeşil mesaj.
8. **Kayıt — Email Çakışması Hatası** — Zaten kayıtlı bir email ile kayıt olunmaya çalışıldığında görünen kırmızı hata mesajı.
9. **Dashboard Sayfası** — `http://localhost:5173/dashboard` adresinin tam ekran görüntüsü. "Hoş geldiniz [AD SOYAD]!" mesajı ve çıkış butonu görünmelidir.
10. **Dashboard — Yükleniyor Durumu** — Dashboard verisi yüklenirken görünen mavi arka planlı "Yükleniyor..." animasyonu.
11. **Mobil Görünüm (Responsive)** — Login veya register sayfasının dar ekran (mobil) görünümü. Sol taraftaki mavi alanın gizlendiğini gösterir.
12. **Playwright Test Sonuçları** — `npx playwright test` komutunun terminal çıktısı (testlerin geçtiğini gösteren).
## Yapay Zeka Entegrasyonu (AI Agents & Skills)

Projenin geliştirme sürecini hızlandırmak, tekrarlayan (boilerplate) işleri otomatize etmek ve kod standartlarını korumak amacıyla sisteme yapay zeka asistan kuralları (AI Skills/Agents) entegre edilmiştir.

* **Frontend Kuralı (`.claude/skills/component.md`):** Proje dizininde oluşturulan bu kural dosyası sayesinde, yapay zekaya `/component` komutu verildiğinde projeye özel standartlarda (Tailwind CSS, Zod validasyonları, React Hook Form ve Axios mimarisine uygun) yeni React bileşenleri üretmesi sağlanmıştır.
* Bu entegrasyon sayesinde arayüz geliştirme süreçlerindeki angarya iş yükü minimize edilmiş ve proje teslim süresi verimli kullanılmıştır.
