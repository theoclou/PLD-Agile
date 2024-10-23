import React from 'react';
import { Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import PropTypes from 'prop-types';

// Define the icon outside the component to avoid recreation on each render
const blackIcon = L.divIcon({
  className: 'black-marker',
  html: '<div style="width: 8px; height: 8px; background-color: darkred; border-radius: 50%;"></div>',
  iconSize: [6, 6],
  iconAnchor: [3, 3],
  popupAnchor: [0, -10],
});

// Component definition first
const CustomMarker = React.memo(({ intersection }) => {
  return (
    <Marker position={[intersection.latitude, intersection.longitude]} icon={blackIcon}>
      <Popup>
        Intersection ID: {intersection.id}
        <br />
        Latitude: {intersection.latitude}
        <br />
        Longitude: {intersection.longitude}
      </Popup>
    </Marker>
  );
});

// PropTypes and displayName after component definition
CustomMarker.propTypes = {
  intersection: PropTypes.shape({
    id: PropTypes.number.isRequired,
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,
  }).isRequired,
};

CustomMarker.displayName = 'CustomMarker';

export default CustomMarker;
