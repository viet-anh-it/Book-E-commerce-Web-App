import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { useState, useEffect } from 'react';
import { ConfigProvider, Layout, theme } from 'antd';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AdminHeader from './components/Header';
import AdminFooter from './components/Footer';
import AdminDrawer from './components/Drawer';
import BookPage from './pages/books/BookPage';
import BookDetail from './pages/books/BookDetail';
import './index.css';

const { Content } = Layout;

/**
 * App Component
 * 
 * Main entry point of the Admin functionality.
 * Handles the configured providers (Theme, Router) and the main Layout structure.
 */
function App() {
  // State to manage the current theme (light or dark)
  const [isDarkMode, setIsDarkMode] = useState(() => {
    return localStorage.getItem('theme') === 'dark';
  });

  useEffect(() => {
    localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
  }, [isDarkMode]);

  // State to manage the collapsed state of the sidebar
  const [collapsed, setCollapsed] = useState(false);

  // Toggle function for theme
  const toggleTheme = () => {
    setIsDarkMode((prev) => !prev);
  };

  return (
    <ConfigProvider
      theme={{
        // Select algorithm based on isDarkMode state
        algorithm: isDarkMode ? theme.darkAlgorithm : theme.defaultAlgorithm,
      }}
    >
      <Router>
        <Layout style={{ minHeight: '100vh' }}>
          {/* Main Navigation Sidebar */}
          <AdminDrawer collapsed={collapsed} />

          <Layout>
            {/* Top Header with Theme Toggle and Sidebar Trigger */}
            <AdminHeader
              isDarkMode={isDarkMode}
              onThemeChange={toggleTheme}
              collapsed={collapsed}
              setCollapsed={setCollapsed}
            />

            {/* Main Content Area */}
            <Content
              style={{
                margin: '24px 16px',
                padding: 24,
                minHeight: 280,
              }}
            >
              {/* 
                  Routes will be defined here. 
                  For now, we just display a welcome message or placeholder.
                */}
              <Routes>
                <Route path="/" element={<div>Welcome to Bookommerce Admin Dashboard</div>} />
                <Route path="/books" element={<BookPage />} />
                <Route path="/books/:id" element={<BookDetail />} />
                {/* Add more routes as needed */}
              </Routes>
            </Content>

            {/* Footer */}
            <AdminFooter />
          </Layout>
        </Layout>
      </Router>
    </ConfigProvider>
  );
}

// Mount the App component
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
);

export default App;
