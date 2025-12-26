const TopBar = () => {
    return (
        /* Top Marketing Bar */
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
    );
};

export default TopBar;