import { FloatButton } from 'antd';

const BackToTopButton = () => {
    return (
        <FloatButton.BackTop
            tooltip="Scroll to Top"
            type="primary"
            style={{ right: 24, bottom: 24 }}
        />
    );
};

export default BackToTopButton;
