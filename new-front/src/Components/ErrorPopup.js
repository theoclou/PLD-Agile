// src/Components/Popup.js
import React from "react";
import "./ErrorPopup.css";
import PropTypes from "prop-types";

const ErrorPopup = ({ message, onClose, text }) => {
  return (
    <div className="popup-overlay">
      <div className="popup">
        <div className="popup-title"> {text} </div>
        <p>{message}</p>
          <div className="button-container">
              <button onClick={onClose}>Close</button>
          </div>
      </div>
    </div>
  );
};

ErrorPopup.propTypes = {
  message: PropTypes.string.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default ErrorPopup;
