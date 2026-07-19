import { Link } from 'react-router-dom';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const loginSchema = z.object({
  email: z.string().min(1, { message: "E-posta alanı zorunludur." }).email({ message: "Geçerli bir e-posta adresi giriniz." }),
  password: z.string().min(6, { message: "Şifre en az 6 karakter olmalıdır." })
});

const LoginForm = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [serverMessage, setServerMessage] = useState({ type: '', text: '' });

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" }
  });

  // Spring Boot 8080 Portu Entegrasyonu
  const onSubmit = async (data) => {
    setServerMessage({ type: '', text: '' });
    
    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
        credentials: "include" 
      });

      if (response.ok) {
        const result = await response.json();
        console.log("Giriş başarılı! Access Token:", result.token);
        setServerMessage({ type: 'success', text: 'Giriş başarılı! Sisteme yönlendiriliyorsunuz...' });
      } else {
        setServerMessage({ type: 'error', text: 'E-posta veya şifre hatalı. Lütfen tekrar deneyin.' });
      }
    } catch (error) {
      console.error("Bağlantı hatası:", error);
      setServerMessage({ type: 'error', text: 'Sunucuya bağlanılamadı. Spring Boot (8080) kapalı olabilir.' });
    }
  };

  return (
    <div className="flex w-full max-w-4xl bg-white rounded-2xl shadow-xl overflow-hidden min-h-[500px]">
      
      {/* Sol Kısım - Mavi Alan ve Görsel */}
      <div className="hidden md:flex flex-col justify-center items-center w-1/2 bg-blue-600 text-white p-12 relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-full bg-blue-700 opacity-20 transform -skew-x-12 -translate-x-20"></div>
        
        <div className="relative z-10 w-full">
          <h2 className="text-4xl font-bold mb-4">Hoş Geldiniz!</h2>
          <p className="text-blue-100 mb-8 text-lg">Projelerinize erişmek için hemen giriş yapın.</p>
          
          <div className="flex justify-center">
          <img 
  src="https://cdni.iconscout.com/illustration/premium/thumb/sign-up-page-4468582-3783955.png" 
  alt="Giriş İllüstrasyonu" 
  className="w-full max-w-sm object-contain drop-shadow-2xl transition-transform hover:scale-105 duration-500"
/>
          </div>
        </div>
      </div>

      {/* Sağ Kısım - Form Alanı */}
      <div className="w-full md:w-1/2 p-12 flex flex-col justify-center">
        <h2 className="text-2xl font-extrabold text-gray-800 mb-1">Giriş Yap</h2>
        <p className="text-sm text-gray-500 mb-8 font-medium">Lütfen bilgilerinizi eksiksiz girin.</p>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          
          {/* E-posta */}
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1.5">E-posta Adresi</label>
            <input 
              type="email"
              {...register("email")}
              className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition-all"
              placeholder="ornek@mail.com"
            />
            {errors.email && <p className="text-red-500 text-xs mt-1.5 font-medium">{errors.email.message}</p>}
          </div>

          {/* Şifre */}
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1.5">Şifre</label>
            <div className="relative">
              <input 
                type={showPassword ? "text" : "password"} 
                {...register("password")}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition-all"
                placeholder="********"
              />
              <span 
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-3.5 text-gray-400 cursor-pointer hover:text-gray-600 select-none"
              >
                {showPassword ? (
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5"><path strokeLinecap="round" strokeLinejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L9.88 9.88" /></svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5"><path strokeLinecap="round" strokeLinejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" /><path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                )}
              </span>
            </div>
            {errors.password && <p className="text-red-500 text-xs mt-1.5 font-medium">{errors.password.message}</p>}
          </div>

          {/* Spring Boot Sunucu Yanıtı Mesaj Alanı */}
          {serverMessage.text && (
            <div className={`p-3 rounded-lg text-sm font-bold text-center ${serverMessage.type === 'error' ? 'bg-red-100 text-red-700 border border-red-200' : 'bg-green-100 text-green-700 border border-green-200'}`}>
              {serverMessage.text}
            </div>
          )}

          {/* Buton */}
          <button 
            type="submit" 
            className="w-full bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition duration-300 mt-4 cursor-pointer shadow-lg shadow-blue-600/30"
          >
            Giriş Yap
          </button>
        </form>

        <p className="text-center text-sm text-gray-600 mt-8 font-medium">
          Hesabınız yok mu? <Link to="/register" className="text-blue-600 font-bold hover:underline">Hemen Kayıt Olun</Link>
        </p>
      </div>

    </div>
  );
};

export default LoginForm;