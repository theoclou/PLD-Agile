import React, { useState, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Rectangle, Polyline } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const MapComponent = () => {
    const [data, setData] = useState({ intersections: [], sections: [] });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [bounds, setBounds] = useState(null);
    const [mapLoaded, setMapLoaded] = useState(false); // Pour suivre si la carte est chargée
    const mapRef = useRef(null); // Référence à la carte

    // Fonction pour fetch les données lors du clic sur le bouton Load Map
    const handleFetchData = async () => {
        setLoading(true); // Active le chargement
        setError(null);   // Réinitialise les erreurs

        try {
            const response = await fetch('http://localhost:8080/map');
            if (!response.ok) {
                throw new Error('Erreur lors de la récupération des données');
            }
            const result = await response.json();
            setData(result);

            // Calculer les coordonnées minimales et maximales
            const latitudes = result.intersections.map(i => i.latitude);
            const longitudes = result.intersections.map(i => i.longitude);

            const minLat = Math.min(...latitudes);
            const maxLat = Math.max(...latitudes);
            const minLng = Math.min(...longitudes);
            const maxLng = Math.max(...longitudes);

            // Ajouter une marge de 0.001
            const margin = 0.001;
            setBounds([
                [minLat - margin, minLng - margin],
                [maxLat + margin, maxLng + margin]
            ]);
            setMapLoaded(true); // Active le statut de la carte chargée
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false); // Désactive le chargement après la requête
        }
    };

    // Fonction pour centrer la carte sur le rectangle bleu
    const handleCenterMap = () => {
        if (mapRef.current && bounds) {
            mapRef.current.fitBounds(bounds); // Centre la carte sur le rectangle
        }
    };

    // Créer une icône noire personnalisée
    const blackIcon = L.divIcon({
        className: 'black-marker',
        html: '<div style="width: 12px; height: 12px; background-color: darkred; border-radius: 50%;"></div>',
        iconSize: [12, 12],
        iconAnchor: [6, 6],
        popupAnchor: [0, -10],
    });

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>Pick'One</h1>
            <div style={styles.buttonContainer}>
                <button style={styles.button} onClick={handleFetchData}>
                    Load Map
                </button>
                <button
                    style={styles.button}
                    onClick={handleCenterMap}
                    disabled={!mapLoaded} // Le bouton est désactivé tant que la carte n'est pas chargée
                >
                    Load Delivery
                </button>
            </div>

            {loading && <div>Loading...</div>}
            {error && <div>Error: {error}</div>}

            {/* Afficher la carte seulement lorsque mapLoaded est true */}
            {mapLoaded && (
                <MapContainer
                    bounds={bounds || [[48.8566, 2.3522], [48.8566, 2.3522]]}
                    style={styles.map}
                    whenCreated={mapInstance => { mapRef.current = mapInstance; }} // Stocker la référence à la carte
                >
                    <TileLayer
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />

                    {/* Afficher les marqueurs et sections si les données sont disponibles */}
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
                        const originId = section.origin;
                        const destinationId = section.destination;

                        const originIntersection = data.intersections.find(i => i.id === originId);
                        const destinationIntersection = data.intersections.find(i => i.id === destinationId);

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

// Styles en ligne pour le composant
const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        width: '100%',
    },
    title: {
        marginBottom: '2%',
        textAlign: 'center',
        fontSize: '60px',
    },
    buttonContainer: {
        display: 'flex',
        justifyContent: 'space-between',
        width: '100%',
        maxWidth: '400px',
        marginBottom: '10px',
    },
    button: {
        padding: '10px 20px',
        fontSize: '16px',
    },
    map: {
        height: '80vh',
        width: '100%',
        border: '2px solid black',
    },
};

export default MapComponent;
