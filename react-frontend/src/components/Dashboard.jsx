import { useNavigate } from 'react-router-dom';
import React, { useEffect, useState } from 'react';
import api from '../api'; // Interceptor olan Axios dosyan

const Dashboard = () => {
  const [fullName, setFullName] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

const handleLogout = () => {
  localStorage.removeItem('accessToken'); // Tarayıcıdaki token'ı siliyoruz
  navigate('/login'); // Kullanıcıyı tekrar login sayfasına yönlendiriyoruz
};

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await api.get('/users/me'); 
        setFullName(`${response.data.firstName} ${response.data.lastName}`);
      } catch (error) {
        console.error("Kullanıcı bilgileri çekilemedi:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  // Veri gelene kadar ekranda kısa bir yükleniyor yazısı gösterelim
  if (loading) {
    return (
      <div className="min-h-screen bg-blue-500 flex items-center justify-center p-4">
        <span className="text-white font-semibold text-2xl animate-pulse">Yükleniyor...</span>
      </div>
    );
  }

  // Veri geldikten sonra gösterilecek asıl ekran
  return (
    <div className="min-h-screen bg-blue-500 flex items-center justify-center p-4">
      <div className="bg-white p-10 md:p-16 rounded-2xl shadow-xl text-center max-w-lg w-full">
        
        <h1 className="text-center text-base md:text-lg font-medium text-gray-600 tracking-wide mb-5 whitespace-nowrap overflow-hidden text-ellipsis">
        Hoşgeldiniz <span className="font-semibold">{fullName ? fullName.toLocaleUpperCase('tr-TR') : ''}</span>!
        </h1>
        
        <p className="text-gray-500 mb-8">
          Sisteme başarıyla giriş yaptınız. Projelerinizi yönetmek için her şey hazır.
        </p>

        <button 
        onClick={handleLogout}
        className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-8 rounded-lg transition duration-300 w-full shadow-lg mt-4"
        >
        Çıkış Yap
        </button>
      </div>
    </div>
  );
};

export default Dashboard;