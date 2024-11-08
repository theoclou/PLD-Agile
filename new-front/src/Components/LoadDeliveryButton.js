import React from 'react';
import "../index.css"

const LoadDeliveryButton = ({ onFileChange }) => {
  return (
    <div className="flex items-center justify-center">
      <input
        type="file"
        id="file-upload-2"
        className="p-1 text-base border border-gray-300 rounded transition duration-300 ease-in-out"
        style={{ display: 'none' }}
        onChange={onFileChange}
      />
      <label
          htmlFor="file-upload-2"
          className="cursor-pointer bg-blue-500 text-white font-semibold py-2 px-4 rounded-lg shadow-md hover:bg-blue-600 transition duration-300">
          Load Delivery
      </label>
    </div>
  );
};

export default LoadDeliveryButton;
