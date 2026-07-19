import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
        <Routes>
          {/* Ana sayfaya girilirse otomatik olarak logine yönlendir */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          {/* /login adresinde LoginForm bileşenini göster */}
          <Route path="/login" element={<LoginForm />} />
          
          {/* /register adresinde RegisterForm bileşenini göster */}
          <Route path="/register" element={<RegisterForm />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
