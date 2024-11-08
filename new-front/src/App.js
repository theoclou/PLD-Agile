import React from 'react';
import './index.css'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MapComponent from './Components/MapComponent';

function App() {
    return (
        <Router>
            <div className="">
                <Routes>
                    <Route
                        path="/"
                        element={
                            <>
                                <div className="">
                                    <MapComponent />
                                </div>
                            </>
                        }
                    />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
