import React from 'react';

const HelperButton = ({ onHelpClick }) => {
    return (
        <button onClick={onHelpClick} className="button">
            Get Help
        </button>
    );
};

export default HelperButton;