import { useState } from 'react'; // React state yönetimini dahil ettik
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

// 1. Zod Kuralları
const loginSchema = z.object({
  email: z.string().min(1, { message: "E-posta alanı zorunludur." }).email({ message: "Geçerli bir e-posta adresi giriniz." }),
  password: z.string().min(6, { message: "Şifre en az 6 karakter olmalıdır." })
});

const LoginForm = () => {
  // 2. Şifre Görünürlüğü İçin State (Başlangıçta gizli: false)
  const [showPassword, setShowPassword] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: ""
    }
  });

  // 3. Form Gönderildiğinde Çalışacak Aksiyon
  const onSubmit = (data) => {
    console.log("Hazırlanan Veri (Spring Boot'a Gidecek):", data);
    alert(`Harika! ${data.email} adresi ile giriş tetiklendi. Detaylar konsolda.`);
  };

  return (
    <div className="flex w-full max-w-4xl bg-white rounded-2xl shadow-xl overflow-hidden min-h-[500px]">
      
      {/* Sol Kısım - Mavi Alan */}
      <div className="hidden md:flex flex-col justify-center w-1/2 bg-blue-600 text-white p-12">
        <h2 className="text-4xl font-bold mb-4">Hoş Geldiniz!</h2>
        <p className="text-blue-100 mb-10 text-lg">Lütfen devam etmek için giriş yapın.</p>
        
        <div className="flex-grow flex items-center justify-center">
          <div className="w-64 h-64 bg-blue-500 rounded-full bg-opacity-50 flex items-center justify-center border-4 border-blue-400 border-dashed">
            <span className="text-blue-200 font-semibold">Görsel Alanı</span>
          </div>
        </div>
      </div>

      {/* Sağ Kısım - Form Alanı */}
      <div className="w-full md:w-1/2 p-12 flex flex-col justify-center">
        <h2 className="text-2xl font-extrabold text-gray-800 mb-1">Giriş Yap</h2>
        <p className="text-sm text-gray-500 mb-8 font-medium">Minimum gereksinimlere uygun giriş ekranı.</p>

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
                // State'e göre tipi 'text' veya 'password' yapıyoruz
                type={showPassword ? "text" : "password"} 
                {...register("password")}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600 transition-all"
                placeholder="********"
              />
              {/* İkona tıklandığında state'i tersine çeviriyoruz */}
              <span 
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-3.5 text-gray-400 cursor-pointer hover:text-gray-600 select-none"
              >
                {showPassword ? (
                  /* Açık Göz İkonu (Çarpı işaretli - Gizlemek için) */
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L9.88 9.88" />
                  </svg>
                ) : (
                  /* Normal Göz İkonu (Göstermek için) */
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                )}
              </span>
            </div>
            {errors.password && <p className="text-red-500 text-xs mt-1.5 font-medium">{errors.password.message}</p>}
          </div>

          {/* Buton */}
          <button 
            type="submit" 
            className="w-full bg-blue-600 text-white font-bold py-3 rounded-lg hover:bg-blue-700 transition duration-300 mt-4 cursor-pointer"
          >
            Giriş Yap
          </button>
        </form>

        <p className="text-center text-sm text-gray-600 mt-8 font-medium">
          Hesabınız yok mu? <a href="#" className="text-blue-600 font-bold hover:underline">Hemen Kayıt Olun</a>
        </p>
      </div>

    </div>
  );
};

export default LoginForm;