// FileUploadButton.js
import React from 'react';

const FileUploadButton = ({ onFileChange }) => {
    return (
        <div className="button">
            <input
                type="file"
                id="file-upload-1"
                className="inputField"
                style={{ display: 'none' }}
                onChange={onFileChange}
            />
            <label htmlFor="file-upload-1" className="button">Load a Map</label>
        </div>
    );
};

export default FileUploadButton;
