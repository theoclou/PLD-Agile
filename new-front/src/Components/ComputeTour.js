// FileUploadButton.js
import React from "react";

const ComputeTour = ({ onClick }) => {
  return (
    <div className="button">
      <button
        id="compute-tour-button"
        className="inputField"
        style={{ display: "none" }}
        onClick={onClick}
      />
      <label htmlFor="compute-tour-button" className="button">
        Compute Tour
      </label>
    </div>
  );
};

export default ComputeTour;
