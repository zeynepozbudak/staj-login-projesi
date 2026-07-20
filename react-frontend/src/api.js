import axios from 'axios';

// 1. Temel bağlantı ayarlarımız
const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true, //HttpOnly cookie'lerin backend'e gitmesini sağlar.
});

// 2. İSTEK (REQUEST) INTERCEPTOR'I: Giden her isteğin içine Access Token ekler
api.interceptors.request.use(
  (config) => {
    // Access token'ı LocalStorage'dan alıyoruz
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. YANIT (RESPONSE) INTERCEPTOR'I: Gelen hataları dinler, 401 ise Refresh Token akışını başlatır
api.interceptors.response.use(
  (response) => response, // İstek başarılıysa aynen yola devam et
  async (error) => {
    const originalRequest = error.config;

    // Eğer backend "401 Unauthorized" (Yetkisiz) verdiyse ve daha önce tekrar denemediysek:
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // Sonsuz döngüye girmemek için işaretliyoruz

      try {
        // Refresh endpoint'e otomatik bir istek atıyoruz
        const refreshResponse = await axios.post(
          'http://localhost:8080/auth/refresh',
          {}, // Body boş
          { withCredentials: true } // Cookie ile refresh token'ı gönderiyoruz
        );

        // Backend'in verdiği yepyeni access token'ı yakalıyoruz
        const newAccessToken = refreshResponse.data.accessToken; 
        
        // Yeni token'ı belleğe kaydediyoruz
        localStorage.setItem('accessToken', newAccessToken);

        // Başarısız olan ilk isteğimizin kafasına (header) yeni token'ı takıp isteği TEKRAR atıyoruz
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
        return api(originalRequest);
        
      } catch (refreshError) {
        // Eğer refresh token'ın da süresi dolduysa yapacak bir şey yok, kullanıcıyı giriş ekranına atıyoruz
        console.error("Oturum tamamen sonlandı, yeniden giriş yapılmalı.");
        localStorage.removeItem('accessToken');
        window.location.href = '/login'; 
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;