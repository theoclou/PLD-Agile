import React from 'react';
import './CourierCounter.css';

const CourierCounter = ({ count, setCount }) => {
    const increment = () => {
        if (count < 10) {
            setCount(prevCount => prevCount + 1);
        }
    };

    const decrement = () => {
        if (count > 2) {
            setCount(prevCount => prevCount - 1);
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

export default CourierCounter;
