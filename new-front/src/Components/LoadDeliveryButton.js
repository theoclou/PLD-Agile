import React from 'react';

const LoadDeliveryButton = ({ onFileChange }) => {
  return (
    <div className="button">
      <input
        type="file"
        id="file-upload-2"
        className="inputField"
        style={{ display: 'none' }}
        onChange={onFileChange}
      />
      <label htmlFor="file-upload-2" className="button">Load Deliveries</label>
    </div>
  );
};

export default LoadDeliveryButton;
