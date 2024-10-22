// FileUploadButton.js
import React from 'react';

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
            <label htmlFor="file-upload-1" className="custom-file-upload">LoadMap</label>
        </div>
    );
};

export default FileUploadButton;
