import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';

const ToastProgressBar = ({ duration, onClose = () => { } }) => {
    return (
        <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            width: '100%',
            height: '4px',
            backgroundColor: 'rgba(0, 0, 0, 0.1)',
            borderBottomLeftRadius: '8px',
            borderBottomRightRadius: '8px',
            overflow: 'hidden'
        }}>
            <motion.div
                initial={{ width: "100%" }}
                animate={{ width: "0%" }}
                transition={{ duration: duration, ease: "linear" }}
                style={{
                    height: '100%',
                    backgroundColor: '#1677ff', // Ant Design Primary Color
                }} // Or use a color prop
                onAnimationComplete={onClose}
            />
        </div>
    );
};

export default ToastProgressBar;
