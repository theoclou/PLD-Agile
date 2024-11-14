import React from "react";
import "./Popup.css";

const CourierSelector = ({ count, setCount, min, max }) => {
  const increment = () => {
    if (count < max) {
      setCount((prevCount) => prevCount + 1);
    }
  };

  const decrement = () => {
    if (count > min) {
      setCount((prevCount) => prevCount - 1);
    }
  };

  return (
    <div className="custom-courier-counter">
      <button
        className="popup-button"
        style={{ padding: "0.5rem" }}
        onClick={decrement}
      >
        -
      </button>
      <h2 className="custom-courier-counter">{count}</h2>
      <button
        className="popup-button"
        style={{ padding: "0.5rem" }}
        onClick={increment}
      >
        +
      </button>
    </div>
  );
};

export default CourierSelector;
