import React from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";

// Define the icon outside the component to avoid recreating it for each rendering
const blackIcon = L.divIcon({
  className: "black-marker",
  html: '<div style="width: 8px; height: 8px; background-color: darkred; border-radius: 50%;"></div>',
  iconSize: [6, 6],
  iconAnchor: [3, 3],
  popupAnchor: [0, -10],
});

// Optimized CustomMarker component
const MapMarker = React.memo(({ intersection, onIntersectionClick }) => {
  const handleClick =() => {
    console.log("Intersection clicked:", intersection.id);
    if (onIntersectionClick && intersection) {
      onIntersectionClick(intersection.id);
    } else {
      console.error("Intersection is undefined:", intersection);
    }
  }
  return (
    <Marker
      position={[intersection.latitude, intersection.longitude]}
      icon={blackIcon} // Use predefined icon
      eventHandlers={{
        click: handleClick, // Add of the click manager
      }}
    >
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

// Props Validation
MapMarker.propTypes = {
  intersection: PropTypes.shape({
    id: PropTypes.number.isRequired,
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,
  }).isRequired,
  onIntersectionClick: PropTypes.func, //Validation of the click function
};

// Display name definition
MapMarker.displayName = "MapMarker";

export default MapMarker;
