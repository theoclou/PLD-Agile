// FileUploadButton.js
import React from 'react';

const AddDeliveryPoint = ({ onClick }) => {
    return (
        <div className="buttonContainer">
            <button
                id="add-delivery-button"
                className="inputField"
                style={{ display: 'none' }}
                onClick={onClick}
            />
                <label htmlFor="add-delivery-button" className="custom-file-upload">Add Delivery Point</label>
        </div>
    );
};

export default AddDeliveryPoint;
