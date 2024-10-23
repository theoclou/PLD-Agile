import React from 'react';
import PropTypes from 'prop-types';

LoadDeliveryButton.propTypes = {
  onLoadDelivery: PropTypes.func.isRequired,
};

const LoadDeliveryButton = ({ onFileChange }) => {
  return (
    <div className="buttonContainer">
      <input
        type="file"
        id="file-upload-1"
        className="inputField"
        style={{ display: 'none' }}
        onChange={onFileChange}
      />
      <label htmlFor="file-upload-1" className="custom-file-upload">
        Load Delivery
      </label>
    </div>
  );
};

export default LoadDeliveryButton;
