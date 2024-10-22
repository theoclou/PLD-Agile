import React from 'react';

const LoadDeliveryButton = ({ onLoadDelivery }) => {
    return (
        <div className="buttonContainer">
            <button className="inputField" onClick={onLoadDelivery}>
                Load Delivery
            </button>
        </div>
    );

};

export default LoadDeliveryButton;
