// FileUploadButton.js
import React from 'react';
import PropTypes from 'prop-types';

const FileUploadButton = ({ onFileChange }) => {
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
        LoadMap
      </label>
    </div>
  );
};

FileUploadButton.propTypes = {
  onFileChange: PropTypes.func.isRequired,
};

export default FileUploadButton;
