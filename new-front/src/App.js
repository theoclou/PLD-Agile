import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MapComponent from './Components/MapComponent';

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/" element={
                        <>
                            <MapComponent />
                        </>
                    } />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
