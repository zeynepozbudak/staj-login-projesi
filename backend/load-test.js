import http from 'k6/http';
import { check, sleep } from 'k6';

// Senaryo: Yavaş yavaş kullanıcı sayısını artır, sonra düşür
export const options = {
    stages: [
        { duration: '10s', target: 10 },   // ilk 10 saniye: 0'dan 10 kullanıcıya çık
        { duration: '20s', target: 30 },   // sonraki 20 saniye: 30 kullanıcıya çık
        { duration: '10s', target: 0 },    // son 10 saniye: sıfıra in
    ],
};

// Her sanal kullanıcı bu fonksiyonu çalıştırır
export default function () {

    var loginBody = JSON.stringify({
        email: 'test@test.com',
        password: 'Test1234!'
    });

    var params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Login endpoint'ine POST isteği at
    var response = http.post('http://localhost:8080/auth/login', loginBody, params);

    // Sonuçları kontrol et
    check(response, {
        'status 200 veya 429 dönmeli': function (r) {
            return r.status === 200 || r.status === 429;
        },
        'yanit suresi 500ms altinda olmali': function (r) {
            return r.timings.duration < 500;
        },
    });

    // Her istek arasında 1 saniye bekle
    sleep(1);
}