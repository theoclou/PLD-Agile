import React, { useRef } from 'react';

const LoadDeliveryButton = ({ onFileChange }) => {
  const fileInputRef = useRef(null);

  const handleButtonClick = () => {
    // Reset the file input value before opening
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
    fileInputRef.current?.click();
  };

  const handleFileChange = (event) => {
    onFileChange(event);
    // Reset the file input after handling the change
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div>
      <input
        ref={fileInputRef}
        type="file"
        onChange={handleFileChange}
        accept=".xml"
        style={{ display: 'none' }}
      />
      <button
        className="button"
        onClick={handleButtonClick}
      >
        Load Deliveries
      </button>
    </div>
  );
};

export default LoadDeliveryButton;