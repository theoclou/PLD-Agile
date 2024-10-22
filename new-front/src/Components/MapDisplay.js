// MapDisplay.js
import React, { useMemo, useEffect } from 'react';
import { MapContainer, TileLayer, Polyline, Popup, useMap } from 'react-leaflet';
import CustomMarker from './CustomMarker';

const MapDisplay = ({ data, bounds, zoom, setZoom }) => {
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
        <MapContainer
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
    );
};

export default MapDisplay;
