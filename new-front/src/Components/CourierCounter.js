import React from "react";
import "./MapComponent.css"

const CourierCounter = ({ count, setCount }) => {
  const increment = () => {
    if (count < 10) {
      setCount((prevCount) => prevCount + 1);
    }
  };

  const decrement = () => {
    if (count > 2) {
      setCount((prevCount) => prevCount - 1);
    }
  };

  return (
      <div className="custom-courier-counter">
        <button className="button" onClick={decrement}>-</button>
        <h2 className="custom-courier-counter">{count} Couriers</h2>
        <button className="button" onClick={increment}>+</button>
      </div>
  );
};

export default CourierCounter;
