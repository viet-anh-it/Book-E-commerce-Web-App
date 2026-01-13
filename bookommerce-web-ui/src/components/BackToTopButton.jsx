import { FloatButton } from 'antd';

const BackToTopButton = () => {
    return (
        <FloatButton.BackTop
            tooltip="Cuộn lên đầu trang"
            type="primary"
            style={{ right: 24, bottom: 24 }}
        />
    );
};

export default BackToTopButton;
