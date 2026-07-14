// Register formu için basit bir kontrol fonksiyonu
const registerForm = document.getElementById('registerForm');

if (registerForm) {
    registerForm.addEventListener('submit', function(e) {
        e.preventDefault(); // Sayfanın yenilenmesini engeller

        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Basit Validasyon (Doğrulama)
        if (password.length < 6) {
            Swal.fire({
                icon: 'error',
                title: 'Hata!',
                text: 'Şifreniz en az 6 karakter olmalıdır.'
            });
            return;
        }

        // Eğer her şey doğruysa, verileri konsola yazdıralım (Backend hazır olduğunda buraya fetch gelecek)
        console.log("Gönderilen Veri:", { firstName, lastName, email, password });
        
        Swal.fire({
            icon: 'success',
            title: 'Başarılı!',
            text: 'Kayıt işlemleri için veriler hazır, sunucu bağlantısı bekleniyor.'
        });
    });
}