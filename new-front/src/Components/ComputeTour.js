// FileUploadButton.js
import React from "react";

const ComputeTour = ({ onClick }) => {
  return (
    <div className="buttonContainer">
      <button
        id="compute-tour-button"
        className="inputField"
        style={{ display: "none" }}
        onClick={onClick}
      />
      <label htmlFor="compute-tour-button" className="custom-file-upload">
        Compute Tour
      </label>
    </div>
  );
};

export default ComputeTour;
