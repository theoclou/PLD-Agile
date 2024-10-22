import React, { useState, useCallback, useMemo, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Polyline, Popup, useMap } from 'react-leaflet';
import CustomMarker from './CustomMarker';
import 'leaflet/dist/leaflet.css';
import './MapComponent.css';

const MapComponent = () => {
    const [data, setData] = useState({ intersections: [], sections: [] });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [bounds, setBounds] = useState(null);
    const [mapLoaded, setMapLoaded] = useState(false);
    const [zoom, setZoom] = useState(8);
    const mapRef = useRef(); // Référence pour le composant MapContainer

    const handleFetchData = useCallback(async () => {
        setLoading(true);
        setError(null);

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

                // Centre la carte sur les nouveaux bounds
                if (mapRef.current) {
                    mapRef.current.fitBounds(newBounds);
                }
            } else {
                throw new Error('Invalid data format from server');
            }
        } catch (error) {
            setError(error.message);
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
                setError(error.message);
            }
        }
    };

    const memoizedIntersections = useMemo(() => data.intersections, [data]);
    const memoizedSections = useMemo(() => data.sections, [data]);

    const ZoomListener = () => {
        const map = useMap();

        useEffect(() => {
            const handleZoomEnd = () => {
                const currentZoom = map.getZoom();
                setZoom(currentZoom);
            };

            map.on('zoomend', handleZoomEnd);
            return () => {
                map.off('zoomend', handleZoomEnd);
            };
        }, [map]);

        return null;
    };

    const filteredIntersections = useMemo(() => {
        const minZoomForIntersections = 18; // Zoom threshold for displaying intersections

        if (zoom >= minZoomForIntersections) {
            return memoizedIntersections;
        }

        return [];
    }, [zoom, memoizedIntersections]);

    return (
        <div className="container">
            <h1 className="title">Pick'One</h1>
            <div className="buttonContainer">
                <input type="file" id="file-upload-1" className="inputField" style={{ display: 'none' }} onChange={handleFileChange} />
                <label htmlFor="file-upload-1" className="custom-file-upload">LoadMap</label>
            </div>

            {loading && <div>Loading...</div>}
            {error && <div>Error: {error}</div>}

            {mapLoaded && (
                <MapContainer
                    ref={mapRef} // Ajoute la référence ici
                    bounds={bounds || [[48.8566, 2.3522], [48.8566, 2.3522]]}
                    zoom={zoom}
                    className="map"
                >
                    <TileLayer
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />
                    <ZoomListener />
                    {filteredIntersections.map((intersection) => (
                        <CustomMarker key={intersection.id} intersection={intersection}>
                            <Popup>
                                Intersection ID: {intersection.id}<br />
                                Latitude: {intersection.latitude}<br />
                                Longitude: {intersection.longitude}
                            </Popup>
                        </CustomMarker>
                    ))}

                    {memoizedSections.map((section, index) => {
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
                </MapContainer>
            )}
        </div>
    );
};

export default MapComponent;
