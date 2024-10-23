// src/Components/Popup.js
import React from "react";
import "./ErrorPopup.css"; // Assurez-vous d'avoir un fichier CSS pour le style
import PropTypes from "prop-types";

const ErrorPopup = ({ message, onClose }) => {
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

ErrorPopup.propTypes = {
  message: PropTypes.string.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ErrorPopup;
