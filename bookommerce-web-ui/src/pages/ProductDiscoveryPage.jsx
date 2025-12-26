import { useState, useEffect } from 'react';
import { Layout, theme, Drawer, Grid } from 'antd';
import { useLocation, useNavigate } from 'react-router-dom';
import ProductFilter from '../components/ProductFilter';
import ProductList from '../components/ProductList';
import ProductControls from '../components/ProductControls';
import PromotionCarousel from '../components/PromotionCarousel';
import BackToTopButton from '../components/BackToTopButton';

const { Content, Sider } = Layout;
const { useBreakpoint } = Grid;

const ProductDiscoveryPage = () => {
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken();
    const screens = useBreakpoint();
    const [filterOpen, setFilterOpen] = useState(false);
    const [totalItems, setTotalItems] = useState(0);
    const location = useLocation();
    const navigate = useNavigate();

    // Initialize activeSections from location state or default to ['1'] (Price)
    const [activeSections, setActiveSections] = useState(() => {
        return location.state?.activeSections || ['1'];
    });

    const handleActiveSectionsChange = (key) => {
        // key can be string or array of strings depending on accordion prop.
        // Ant Design Collapse onChange returns key[] or string.
        // Since we use accordion={true}, it returns string or empty array?
        // Actually for accordion, it returns the active key string or undefined/null/empty string.
        // But activeKey prop expects string[] or string.
        // Let's ensure we handle it correctly.
        // If accordion is true, key is a string (or undefined if collapsed).
        // We'll store it as an array to be consistent with activeKey prop expectation if we ever switch off accordion.
        const newActiveSections = Array.isArray(key) ? key : (key ? [key] : []);
        setActiveSections(newActiveSections);

        // Update location state to preserve this change
        navigate({
            pathname: '.',
            search: location.search
        }, {
            state: {
                ...location.state,
                activeSections: newActiveSections
            },
            replace: true
        });
    };

    const isMobile = !screens.lg;

    return (
        <>
            <PromotionCarousel />
            <Layout style={{ marginTop: 24, marginBottom: 48, padding: isMobile ? '0 16px' : '0 24px', alignItems: 'flex-start' }}>
                {!isMobile && (
                    <Sider
                        width={300}
                        className="no-scrollbar"
                        style={{
                            background: colorBgContainer,
                            borderRight: '1px solid rgba(0, 0, 0, 0.06)',
                            height: 'calc(100vh - 88px - 24px)', // Viewport height - Header (64px) - Top Margin (24px) - Bottom Margin (24px)
                            position: 'sticky',
                            top: 88, // Header (64px) + Top Margin (24px)
                            overflow: 'auto',
                            borderRadius: borderRadiusLG,
                            marginRight: 24,
                        }}
                    >
                        <ProductFilter
                            activeSections={activeSections}
                            onActiveSectionsChange={handleActiveSectionsChange}
                        />
                    </Sider>
                )}

                {isMobile && (
                    <Drawer
                        title="Filters"
                        placement="left"
                        onClose={() => setFilterOpen(false)}
                        open={filterOpen}
                        width={300}
                        bodyStyle={{ padding: 0 }}
                    >
                        <ProductFilter
                            activeSections={activeSections}
                            onActiveSectionsChange={handleActiveSectionsChange}
                        />
                    </Drawer>
                )}

                <Content
                    style={{
                        padding: 24,
                        margin: 0,
                        minHeight: 280,
                        background: colorBgContainer,
                        borderRadius: borderRadiusLG,
                        flex: 1, // Take remaining width
                        width: '100%', // Ensure it takes width in flex container
                        overflow: 'hidden', // Prevent overflow
                    }}
                >
                    <ProductList
                        onProductsChange={setTotalItems}
                    />
                </Content>

                {!isMobile && (
                    <Sider
                        width={300}
                        className="no-scrollbar"
                        style={{
                            background: colorBgContainer,
                            borderLeft: '1px solid rgba(0, 0, 0, 0.06)',
                            height: 'calc(100vh - 88px - 24px)',
                            position: 'sticky',
                            top: 88,
                            overflow: 'auto',
                            borderRadius: borderRadiusLG,
                            marginLeft: 24,
                        }}
                    >
                        <ProductControls key={totalItems} totalItems={totalItems} />
                    </Sider>
                )}
                <BackToTopButton />
            </Layout>
        </>
    );
};

export default ProductDiscoveryPage;
