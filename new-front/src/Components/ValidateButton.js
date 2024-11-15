// FileUploadButton.js
import React from "react";

const ValidateButton = ({ onClick }) => {
  return (
    <div className="button">
      <button
        id="validate-tour-button"
        className="inputField"
        style={{ display: "none" }}
        onClick={onClick}
      />
      <label htmlFor="validate-tour-button" className="button">
        Export Tour
      </label>
    </div>
  );
};

export default ValidateButton;
