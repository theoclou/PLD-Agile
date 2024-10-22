// src/Components/Popup.js
import React from 'react';
import './Popup.css'; // Assurez-vous d'avoir un fichier CSS pour le style
import PropTypes from 'prop-types';

Popup.propTypes = {
  message: PropTypes.string.isRequired,
  onClose: PropTypes.func.isRequired,
};

const Popup = ({ message, onClose }) => {
  return (
    <div className="popup-overlay">
      <div className="popup">
        <h2>Error</h2>
        <p>{message}</p>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default Popup;
