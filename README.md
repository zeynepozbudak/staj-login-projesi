# Staj Login Projesi 

Bu proje, bir tam yığın (full-stack) web uygulamasının giriş ve kayıt süreçlerini içeren, üniversite stajı kapsamında geliştirilmiş bir çalışmadır.

## Proje Hakkında
"The Login Page" e-ticaret web uygulaması için geliştirilen bu modül, kullanıcı güvenliğini ve arayüz deneyimini ön planda tutan bir giriş ve kayıt ekranı sunmaktadır.

## Kullanılan Teknolojiler

### Frontend
- **Yapı:** HTML5
- **Stil:** Tailwind CSS (Modern ve responsive tasarım için)
- **Etkileşim:** JavaScript (Şifre gizleme/gösterme mantığı ve form validasyonları)
- **Uyarılar:** SweetAlert2 (Kullanıcı dostu bildirimler için)
- **İkonlar:** FontAwesome

### Backend
- **Yapı:** (Buraya kullandığın Node.js/Express vb. teknolojiyi yaz)
- **Güvenlik:** Token tabanlı kimlik doğrulama (Authentication) stratejisi

## Proje Yapısı
- `/frontend`: Kullanıcı arayüzü dosyaları (`index.html`, `register.html`, `main.js` vb.).
- `/backend`: Sunucu tarafı API ve veritabanı bağlantı kodları.

## Geliştirme Notları
- Docker ve WSL2 ortamları kullanılarak geliştirilmiştir.
- Frontend ve backend entegrasyonu, token yönetimi üzerinden gerçekleştirilmektedir.
