import RegisterForm from './components/RegisterForm'; // LoginForm yerine bunu import ettik

function App() {
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      {/* Şimdilik ekrana RegisterForm basıyoruz */}
      <RegisterForm />
    </div>
  )
}

export default App;
