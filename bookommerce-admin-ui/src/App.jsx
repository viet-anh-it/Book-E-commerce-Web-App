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
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ForbiddenPage from './pages/ForbiddenPage';
import ProtectedRoute from './components/ProtectedRoute';
import './index.css';

const { Content } = Layout;

/**
 * App Component
 * 
 * Main entry point of the Admin functionality.
 * Handles the configured providers (Theme, Router) and the main Layout structure.
 */
const AdminAppInner = ({ isDarkMode, toggleTheme, collapsed, setCollapsed }) => {
    const { user, loading } = useAuth();

    // If user has ROLE_CUSTOMER, they are not allowed in Admin UI
    // We render ForbiddenPage full-screen here (outside the main Layout)
    if (!loading && user && user.authorities?.includes('ROLE_CUSTOMER')) {
        return <ForbiddenPage />;
    }

    return (
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
                    <Routes>
                        <Route path="/" element={<ProtectedRoute><div>Welcome to Bookommerce Admin Dashboard</div></ProtectedRoute>} />
                        <Route path="/books" element={<ProtectedRoute><BookPage /></ProtectedRoute>} />
                        <Route path="/books/:id" element={<ProtectedRoute><BookDetail /></ProtectedRoute>} />
                        {/* Add more routes as needed */}
                    </Routes>
                </Content>

                {/* Footer */}
                <AdminFooter />
            </Layout>
        </Layout>
    );
};

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
                <AuthProvider>
                    <AdminAppInner
                        isDarkMode={isDarkMode}
                        toggleTheme={toggleTheme}
                        collapsed={collapsed}
                        setCollapsed={setCollapsed}
                    />
                </AuthProvider>
            </Router>
        </ConfigProvider>
    );
}

// Mount the App component
createRoot(document.getElementById('root')).render(
    <App />,
);

export default App;
