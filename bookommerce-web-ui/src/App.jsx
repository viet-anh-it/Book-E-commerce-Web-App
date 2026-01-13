import { ConfigProvider, Layout, theme } from 'antd';
import viVN from 'antd/locale/vi_VN';
import PropTypes from 'prop-types';
import { useState } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AppFooter from './components/Footer';
import Header from './components/Header';
import ProtectedRoute from './components/ProtectedRoute';
import ScrollToTop from './components/ScrollToTop';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import './css/index.css';
import CartPage from './pages/CartPage';
import ForbiddenPage from './pages/ForbiddenPage';
import ProductDetailPage from './pages/ProductDetailPage';
import ProductDiscoveryPage from './pages/ProductDiscoveryPage';
import ProfilePage from './pages/ProfilePage';

const App = () => {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    const savedTheme = localStorage.getItem('theme');
    return savedTheme === 'dark';
  });

  const toggleTheme = () => {
    const newTheme = !isDarkMode;
    setIsDarkMode(newTheme);
    localStorage.setItem('theme', newTheme ? 'dark' : 'light');
  };

  return (
    <ConfigProvider
      locale={viVN}
      theme={{
        algorithm: isDarkMode ? theme.darkAlgorithm : theme.defaultAlgorithm,
      }}
    >
      <AppContent isDarkMode={isDarkMode} toggleTheme={toggleTheme} />
    </ConfigProvider>
  );
};

const AppInner = ({ isDarkMode, toggleTheme }) => {
  const { user } = useAuth();

  if (user && !user.authorities?.includes('ROLE_CUSTOMER')) {
    return <ForbiddenPage />;
  }

  return (
    <BrowserRouter>
      <ScrollToTop />
      <Layout style={{ minHeight: '100vh' }}>
        {/* Top Marketing Bar */}
        <div style={{
          backgroundColor: '#f5f5f5',
          textAlign: 'center',
          lineHeight: 0, // Remove extra space below image
        }}>
          <img
            src="https://cdn1.fahasa.com/media/wysiwyg/Thang-11-2025/Homepage_T11_1263x60_BachViet.png"
            alt="Marketing Banner"
            style={{ width: '100%', height: 'auto', display: 'block', objectFit: 'cover' }}
          />
        </div>
        <Header isDarkMode={isDarkMode} onToggleTheme={toggleTheme} />
        <Routes>
          <Route path="/" element={<ProductDiscoveryPage />} />
          <Route path="/books/:id" element={<ProductDetailPage />} />
          <Route path="/cart" element={
            <ProtectedRoute>
              <CartPage />
            </ProtectedRoute>
          } />
          <Route path="/profile" element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          } />
        </Routes>
        <AppFooter />
      </Layout>
    </BrowserRouter>
  );
};

const AppContent = ({ isDarkMode, toggleTheme }) => {
  return (
    <AuthProvider>
      <AppInner isDarkMode={isDarkMode} toggleTheme={toggleTheme} />
    </AuthProvider>
  );
};

AppInner.propTypes = {
  isDarkMode: PropTypes.bool.isRequired,
  toggleTheme: PropTypes.func.isRequired,
};

AppContent.propTypes = {
  isDarkMode: PropTypes.bool.isRequired,
  toggleTheme: PropTypes.func.isRequired,
};

createRoot(document.getElementById('root')).render(
  <App />
)