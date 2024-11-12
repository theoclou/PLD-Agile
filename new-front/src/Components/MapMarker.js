import React, { useState } from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";

// Define the icons outside the component
const blackIcon = L.divIcon({
  className: "black-marker",
  html: '<div style="width: 14px; height: 14px; background-color: darkred; border-radius: 50%; transition: all 0.2s;"></div>',
  iconSize: [14, 14],
  iconAnchor: [7, 7],
  popupAnchor: [0, -10],
});

const highlightedIcon = L.divIcon({
  className: "black-marker-highlighted",
  html: '<div style="width: 20px; height: 20px; background-color: red; border-radius: 50%; box-shadow: 0 0 15px rgba(255, 0, 0, 0.6); transition: all 0.2s;"></div>',
  iconSize: [20, 20],
  iconAnchor: [10, 10],
  popupAnchor: [0, -12],
});

const MapMarker = React.memo(({ intersection, onAddDeliveryPoint, setWarehouse, hasDeliveries }) => {
  const [isHighlighted, setIsHighlighted] = useState(false);

  return (
      <Marker
          position={[intersection.latitude, intersection.longitude]}
          icon={isHighlighted ? highlightedIcon : blackIcon}
          eventHandlers={{
            mouseover: () => {
              setIsHighlighted(true);
            },
            mouseout: () => {
              setIsHighlighted(false);
            },
          }}
      >
        <Popup>
          <div>
            Intersection ID: {intersection.id}
            <br/>
            Latitude: {intersection.latitude}
            <br/>
            Longitude: {intersection.longitude}
            <br/>

            {hasDeliveries && (<button onClick={() => onAddDeliveryPoint(intersection.id)}>
                                    Add to delivery points
                                </button>
            )}


            {!hasDeliveries && (
                <button onClick={() => setWarehouse(intersection.id)}>
                  Define as warehouse
                </button>
            )}
          </div>
        </Popup>
      </Marker>
  );
});

MapMarker.propTypes = {
  intersection: PropTypes.shape({
    id: PropTypes.string.isRequired,
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,
  }).isRequired,
  onAddDeliveryPoint: PropTypes.func.isRequired,
};

MapMarker.displayName = "MapMarker";

export default MapMarker;
