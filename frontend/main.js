// --- YENİ EKLENECEK KISIM ---
let accessToken = ""; // Memory'de tutulacak token

// Meryem'in bahsettiği authFetch fonksiyonu
async function authFetch(url, options = {}) {
    options.headers = {
        ...options.headers,
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
    };

    let response = await fetch(url, options);

    // Eğer 401 hatası alırsan token'ı yenilemeyi dene
    if (response.status === 401) {
        await refreshAccessToken();
        options.headers['Authorization'] = `Bearer ${accessToken}`;
        response = await fetch(url, options);
    }
    return response;
}

// Token yenileme fonksiyonu
async function refreshAccessToken() {
    try {
        const response = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            credentials: 'include' 
        });
        
        if (response.ok) {
            const data = await response.json();
            accessToken = data.accessToken;
        } else {
            window.location.href = 'login.html';
        }
    } catch (error) {
        window.location.href = 'login.html';
    }
}
// 1. Göz İkonu ve Şifre Göster/Gizle Mantığı
const togglePassword = document.querySelector('#togglePassword');
const passwordField = document.querySelector('#password');

if (togglePassword && passwordField) {
    togglePassword.addEventListener('click', function () {
        // Şifre tipini değiştir
        const type = passwordField.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordField.setAttribute('type', type);
        
        // İkonu değiştir (fa-eye <-> fa-eye-slash)
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });
}

// 2. Register Formu Kontrolü
const registerForm = document.getElementById('registerForm');

if (registerForm) {
    registerForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const email = document.getElementById('email').value;
        const password = passwordField.value; // Yukarıda tanımladığımız değişkeni kullan

        if (password.length < 8) {
            Swal.fire({
                icon: 'error',
                title: 'Hata!',
                text: 'Şifreniz en az 8 karakter olmalıdır.'
            });
            return;
        }

        // ... (Validasyon kısmı aynı kalacak)

        // ESKİ KOD: console.log("Gönderilen Veri:", { firstName, lastName, email, password });
        
        // YENİ KOD: API İsteği
        fetch('http://localhost:8080/auth/register', { // Meryem ile URL'i teyit et
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                firstName: firstName, 
                lastName: lastName, 
                email: email, 
                password: password 
            }),
        })
        .then(response => {
            if (!response.ok) throw new Error('Kayıt başarısız');
            return response.json();
        })
        .then(data => {
            Swal.fire({
                icon: 'success',
                title: 'Başarılı!',
                text: 'Hesabınız başarıyla oluşturuldu.'
            });
        })
        .catch(error => {
            Swal.fire({
                icon: 'error',
                title: 'Hata!',
                text: 'Kayıt sırasında bir sorun oluştu: ' + error.message
            });
        });
    });
}
// --- EKSİK OLAN FONKSİYONLAR ---

// 3. Sayfa yüklendiğinde otomatik oturum kontrolü
async function initAuth() {
    try {
        const response = await fetch('http://localhost:8080/auth/refresh', {
            method: 'POST',
            credentials: 'include'
        });
        
        if (response.ok) {
            const data = await response.json();
            accessToken = data.accessToken;
            console.log("Oturum yenilendi.");
        } else {
            // Eğer dashboard'daysan ve yetkin yoksa login'e at
            if (window.location.pathname.includes('dashboard.html')) {
                window.location.href = 'login.html';
            }
        }
    } catch (error) {
        console.error("Auth hatası:", error);
    }
}

// 4. Çıkış yapma fonksiyonu
async function logout() {
    try {
        await fetch('http://localhost:8080/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });
        accessToken = "";
        window.location.href = 'login.html';
    } catch (error) {
        console.error("Çıkış hatası:", error);
    }
}

// 5. Sayfa yüklendiğinde dashboard'u kontrol et ve çıkış butonunu dinle
document.addEventListener('DOMContentLoaded', () => {
    // Sadece dashboard sayfasındaysan initAuth'u çalıştır
    if (window.location.pathname.includes('dashboard.html')) {
        initAuth();
    }

    // Eğer sayfada çıkış butonu varsa tıklandığında logout'u çalıştır
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});// 6. Login Formu Kontrolü
const loginForm = document.getElementById('loginForm');

if (loginForm) {
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault(); // Sayfa yenilenmesini engelle

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value; 

        // Meryem ile URL'in sonunu (/auth/login) teyit etmen iyi olabilir
        fetch('http://localhost:8080/auth/login', { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                email: email, 
                password: password 
            }),
        })
        .then(response => {
            if (!response.ok) throw new Error('Giriş başarısız');
            return response.json(); // Backend'in döndürdüğü token'ı al
        })
        .then(data => {
            // Meryem'in gönderdiği token'ı global değişkene kaydet
            accessToken = data.accessToken; 
            
            Swal.fire({
                icon: 'success',
                title: 'Hoş Geldiniz!',
                text: 'Giriş başarılı, yönlendiriliyorsunuz...',
                timer: 1500, // 1.5 saniye sonra uyarı otomatik kapansın
                showConfirmButton: false
            }).then(() => {
                // Başarılı giriş sonrası dashboard'a geç
                window.location.href = 'dashboard.html';
            });
        })
        .catch(error => {
            Swal.fire({
                icon: 'error',
                title: 'Hata!',
                text: 'E-posta veya şifre hatalı. Lütfen tekrar deneyin.'
            });
        });
    });
}
