import React from 'react';
import PropTypes from 'prop-types';
import './CourierCounter.css';

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
    <div className="courier-counter">
      <h2>Courriers: {count}</h2>
      <button onClick={decrement}>-</button>
      <button onClick={increment}>+</button>
    </div>
  );
};

CourierCounter.propTypes = {
  count: PropTypes.number.isRequired,
  setCount: PropTypes.func.isRequired,
};
export default CourierCounter;
