import React, { useState, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Rectangle, Polyline } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import './MapComponent.css'; // Import du fichier CSS

const MapComponent = () => {
    const [data, setData] = useState({ intersections: [], sections: [] });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [bounds, setBounds] = useState(null);
    const [mapLoaded, setMapLoaded] = useState(false);
    const [nombreLivreurs, setNombreLivreurs] = useState(2);
    const [file1, setFile1] = useState(null);
    const [fileName1, setFileName1] = useState('Choose a File');
    const [file2, setFile2] = useState(null);
    const [fileName2, setFileName2] = useState('Choose a File');
    const mapRef = useRef(null);

    const handleFetchData = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch('http://localhost:8080/map');
            if (!response.ok) {
                throw new Error('Erreur lors de la récupération des données');
            }
            const result = await response.json();
            setData(result);

            const latitudes = result.intersections.map(i => i.latitude);
            const longitudes = result.intersections.map(i => i.longitude);

            const minLat = Math.min(...latitudes);
            const maxLat = Math.max(...latitudes);
            const minLng = Math.min(...longitudes);
            const maxLng = Math.max(...longitudes);

            const margin = 0.001;
            setBounds([
                [minLat - margin, minLng - margin],
                [maxLat + margin, maxLng + margin]
            ]);
            setMapLoaded(true);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const blackIcon = L.divIcon({
        className: 'black-marker',
        html: '<div style="width: 12px; height: 12px; background-color: darkred; border-radius: 50%;"></div>',
        iconSize: [12, 12],
        iconAnchor: [6, 6],
        popupAnchor: [0, -10],
    });

    const handleIncrease = () => {
        setNombreLivreurs(prevCount => prevCount + 1);
    };

    const handleDecrease = () => {
        if (nombreLivreurs > 2) {
            setNombreLivreurs(prevCount => prevCount - 1);
        }
    };

    const handleFileChange1 = (event) => {
        const selectedFile = event.target.files[0];
        setFile1(selectedFile);
        if (selectedFile) {
            setFileName1(selectedFile.name);
        } else {
            setFileName1('Choose a File');
        }
    };

    const handleFileChange2 = (event) => {
        const selectedFile = event.target.files[0];
        setFile2(selectedFile);
        if (selectedFile) {
            setFileName2(selectedFile.name);
        } else {
            setFileName2('Choose a File');
        }
    };

    return (
        <div className="container">
            <h1 className="title">Pick'One</h1>

            <br />
            <br />

            <div className="buttonContainer">
                <button className="button" onClick={handleFetchData}>
                    Load Map
                </button>
                ---->
                <input type="file" id="file-upload-1" className="inputField" style={{ display: 'none' }} onChange={handleFileChange1} />
                <label htmlFor="file-upload-1" className="custom-file-upload">
                    {fileName1}
                </label>
            </div>

            <div className="buttonContainer">
                <button
                    className="button"
                    disabled={!mapLoaded}
                >
                    Load Delivery
                </button>
                ---->
                <input type="file" id="file-upload-2" className="inputField" style={{ display: 'none' }} onChange={handleFileChange2} />
                <label htmlFor="file-upload-2" className="custom-file-upload">
                    {fileName2}
                </label>
            </div>

            <div className="buttonContainer">
                <button
                    className="button"
                    disabled={!mapLoaded}
                >
                    Compute
                </button>
            </div>

            <br />

            <div className="buttonContainer">
                <button className="button" onClick={handleDecrease}>
                    -
                </button>
                <span className="courierCounter">
                    Couriers: {nombreLivreurs}
                </span>
                <button className="button" onClick={handleIncrease}>
                    +
                </button>
            </div>

            {loading && <div>Loading...</div>}
            {error && <div>Error: {error}</div>}

            {mapLoaded && (
                <MapContainer
                    bounds={bounds || [[48.8566, 2.3522], [48.8566, 2.3522]]}
                    className="map"
                    whenCreated={mapInstance => { mapRef.current = mapInstance; }}
                >
                    <TileLayer
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />

                    {data.intersections.map((intersection) => (
                        <Marker
                            key={intersection.id}
                            position={[intersection.latitude, intersection.longitude]}
                            icon={blackIcon}
                        >
                            <Popup>
                                Intersection ID: {intersection.id}<br />
                                Latitude: {intersection.latitude}<br />
                                Longitude: {intersection.longitude}
                            </Popup>
                        </Marker>
                    ))}

                    {data.sections.map((section, index) => {
                        const originIntersection = section.origin;
                        const destinationIntersection = section.destination;

                        if (originIntersection && destinationIntersection) {
                            const latLngs = [
                                [originIntersection.latitude, originIntersection.longitude],
                                [destinationIntersection.latitude, destinationIntersection.longitude]
                            ];

                            return (
                                <Polyline
                                    key={index}
                                    positions={latLngs}
                                    color="red"
                                    weight={2}
                                    opacity={1}
                                />
                            );
                        }
                        return null;
                    })}

                    {bounds && <Rectangle bounds={bounds} color="blue" fillOpacity={0.1} />}
                </MapContainer>
            )}
        </div>
    );
};

export default MapComponent;
