import React, { useState, useCallback, useRef } from 'react';
import MapDisplay from './MapDisplay';
import FileUploadButton from './FileUploadButton';
import Popup from './Popup';
import LoadDeliveryButton from './LoadDeliveryButton';
import CourierCounter from './CourierCounter';
import 'leaflet/dist/leaflet.css';
import './MapComponent.css';

const MapComponent = () => {
    const [data, setData] = useState({ intersections: [], sections: [] });
    const [loading, setLoading] = useState(false);
    const [bounds, setBounds] = useState(null);
    const [mapLoaded, setMapLoaded] = useState(false);
    const [zoom, setZoom] = useState(8);
    const mapRef = useRef(); // Référence pour le composant MapContainer
    const [popupVisible, setPopupVisible] = useState(false);
    const [popupMessage, setPopupMessage] = useState('');
    const [courierCount, setCourierCount] = useState(2); // État pour le nombre de courriers

    const handleFetchData = useCallback(async () => {
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/map');
            if (!response.ok) throw new Error('Error during data retrieval');

            const result = await response.json();
            if (result && result.intersections) {
                setData(result);
                const latitudes = result.intersections.map(i => i.latitude);
                const longitudes = result.intersections.map(i => i.longitude);
                const newBounds = [
                    [Math.min(...latitudes), Math.min(...longitudes)],
                    [Math.max(...latitudes), Math.max(...longitudes)]
                ];
                setBounds(newBounds);
                setMapLoaded(true);

                if (mapRef.current) {
                    mapRef.current.fitBounds(newBounds);
                }
            } else {
                throw new Error('Invalid data format from server');
            }
        } catch (error) {
            setPopupMessage(error.message);
            setPopupVisible(true); // Affiche le pop-up
        } finally {
            setLoading(false);
        }
    }, []);

    const handleFileChange = async (event) => {
        const selectedFile = event.target.files[0];

        if (selectedFile) {
            const formData = new FormData();
            formData.append("file", selectedFile);

            try {
                const response = await fetch('http://localhost:8080/loadMap', {
                    method: 'POST',
                    body: formData,
                });
                if (!response.ok) throw new Error('Failed to upload file, try again');
                await handleFetchData();
            } catch (error) {
                setPopupMessage(error.message);
                setPopupVisible(true);
            }
        }
    };

    const handleClosePopup = () => {
        setPopupVisible(false);
    };

    const close = async () => {
        console.log("Load Delivery button clicked!");
        try {
            await fetch('http://localhost:8080/courriers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ count: courierCount }),
            });
        } catch (error) {
            console.error("Error updating courier count:", error);
        }
    };

    const handleLoadDelivery = async (event) => {
        const selectedFile = event.target.files[0];

        if (selectedFile) {
            console.log(`File Name: ${selectedFile.name}`);
            const formData = new FormData();
            formData.append("file", selectedFile);


            try {
                const response = await fetch('http://localhost:8080/loadDelivery', {
                    method: 'POST',
                    body: formData,
                });
                if (!response.ok) throw new Error('Failed to upload file, try again');
            } catch (error) {
                setPopupMessage(error.message);
                setPopupVisible(true);
            }
        }
    };

    return (
        <div className="container">
            <h1 className="title">Pick'One</h1>
            <FileUploadButton onFileChange={handleFileChange} />

            <LoadDeliveryButton onFileChange={handleLoadDelivery} />

            <CourierCounter count={courierCount} setCount={setCourierCount} />

            {loading && <div>Loading...</div>}

            {mapLoaded && (
                <MapDisplay
                    data={data}
                    bounds={bounds}
                    zoom={zoom}
                    setZoom={setZoom}
                />
            )}

            {popupVisible && (
                <Popup message={popupMessage} onClose={handleClosePopup} />
            )}
        </div>
    );
};

export default MapComponent;
