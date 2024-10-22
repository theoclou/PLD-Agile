import React from 'react';
import PropTypes from 'prop-types';

LoadDeliveryButton.propTypes = {
  onLoadDelivery: PropTypes.func.isRequired,
};

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
