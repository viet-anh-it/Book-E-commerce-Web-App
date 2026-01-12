import { Spin } from 'antd';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ children }) => {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Spin size="large" />
            </div>
        );
    }

    if (!user) {
        window.location.href = 'https://bff.bookommerce.com:8181/protected/oauth2/authorization/bff';
        return null;
    }

    return children;
};

export default ProtectedRoute;
