import React from 'react';
import { Carousel, Row, Col, theme, Grid, Button } from 'antd';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';

const { useBreakpoint } = Grid;

const PromotionCarousel = () => {
    const {
        token: { borderRadiusLG, colorBgContainer, colorText },
    } = theme.useToken();
    const screens = useBreakpoint();

    const containerStyle = {
        margin: '24px 24px 0 24px',
    };

    const imageStyle = {
        width: '100%',
        height: '100%',
        objectFit: 'contain',
        borderRadius: borderRadiusLG,
        display: 'block',
    };

    // Responsive heights
    const isMobile = !screens.md;
    const carouselHeight = isMobile ? 'auto' : '328px';
    const sideBannerHeight = isMobile ? 'auto' : '156px';
    const bottomBannerHeight = isMobile ? 'auto' : '210px';

    // Custom Arrows
    const ArrowButton = ({ currentSlide, slideCount, ...props }) => {
        const { onClick, direction } = props;
        return (
            <Button
                shape="circle"
                icon={direction === 'left' ? <LeftOutlined /> : <RightOutlined />}
                onClick={onClick}
                style={{
                    position: 'absolute',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    zIndex: 10,
                    [direction === 'left' ? 'left' : 'right']: 16,
                    backgroundColor: colorBgContainer,
                    color: colorText,
                    border: 'none',
                    opacity: 0.9,
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                }}
            />
        );
    };

    // Carousel images
    const carouselImages = [
        "https://cdn1.fahasa.com/media/magentothem/banner7/CTT11_BlackFriday_840x320.png",
        "https://cdn1.fahasa.com/media/magentothem/banner7/MangaDarkFriday_840x320.png",
        "https://cdn1.fahasa.com/media/magentothem/banner7/RESIZE_MCbooksT11_840x320.png",
        "https://cdn1.fahasa.com/media/magentothem/banner7/PartnershipT11_Resize_840x320.png",
        "https://cdn1.fahasa.com/media/magentothem/banner7/BannerMayoraT11_840x320_v2.png"
    ];

    // Side banners
    const sideBanner1 = "https://cdn1.fahasa.com/media/wysiwyg/Thang-11-2025/ZaloPayT11_392x156.jpg";
    const sideBanner2 = "https://cdn1.fahasa.com/media/wysiwyg/Thang-11-2025/ShoppePay11_%20392x156.jpg";

    // Bottom banners
    const bottomBanners = [
        "https://cdn1.fahasa.com/media/wysiwyg/Thang-12-2025/Homepage_T12_small_TanViet.png",
        "https://cdn1.fahasa.com/media/wysiwyg/Thang-12-2025/NgoaiVan_t12_310x210.png",
        "https://cdn1.fahasa.com/media/wysiwyg/Thang-12-2025/DoChoiChoTre_310x210.png",
        "https://cdn1.fahasa.com/media/wysiwyg/Thang-12-2025/Homepage_T12_1GiangSinh.png"
    ];

    return (
        <div style={containerStyle}>
            {/* Top Section: Carousel (2/3) + Side Banners (1/3) */}
            <Row gutter={[16, 16]}>
                <Col xs={24} md={16}>
                    <Carousel
                        autoplay
                        arrows
                        prevArrow={<ArrowButton direction="left" />}
                        nextArrow={<ArrowButton direction="right" />}
                        style={{ borderRadius: borderRadiusLG, overflow: 'hidden' }}
                    >
                        {carouselImages.map((src, index) => (
                            <div key={index}>
                                <div style={{ height: carouselHeight, width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#fff' }}>
                                    <img
                                        src={src}
                                        alt={`Promotion ${index + 1}`}
                                        style={imageStyle}
                                    />
                                </div>
                            </div>
                        ))}
                    </Carousel>
                </Col>
                <Col xs={24} md={8}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', height: '100%' }}>
                        <div style={{ height: sideBannerHeight, borderRadius: borderRadiusLG, overflow: 'hidden', backgroundColor: '#fff' }}>
                            <img src={sideBanner1} alt="Banner 1" style={imageStyle} />
                        </div>
                        <div style={{ height: sideBannerHeight, borderRadius: borderRadiusLG, overflow: 'hidden', backgroundColor: '#fff' }}>
                            <img src={sideBanner2} alt="Banner 2" style={imageStyle} />
                        </div>
                    </div>
                </Col>
            </Row>

            {/* Bottom Section: 4 Banners */}
            <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
                {bottomBanners.map((src, index) => (
                    <Col key={index} xs={12} sm={12} md={6}>
                        <div style={{ height: bottomBannerHeight, borderRadius: borderRadiusLG, overflow: 'hidden', backgroundColor: '#fff' }}>
                            <img src={src} alt={`Banner ${index + 3}`} style={imageStyle} />
                        </div>
                    </Col>
                ))}
            </Row>
        </div>
    );
};

export default PromotionCarousel;