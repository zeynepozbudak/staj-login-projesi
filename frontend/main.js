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
// ... (Kodun geri kalanı aynı)
        
        Swal.fire({
            icon: 'success',
            title: 'Başarılı!',
            text: 'Veriler hazır, backend entegrasyonu için hazırsınız.'
        });
    });
}
