import React from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";
import warehouseMarker from "../Assets/warehouseMarker.png";
import "./Popup.css";

const icon = L.icon({
  iconUrl: warehouseMarker,
  iconSize: [32, 32], // size of the icon
  iconAnchor: [8, 8], // point of the icon which will correspond to marker's location
  popupAnchor: [0, -10], // point from which the popup should open relative to the iconAnchor
});

// Composant DeliveryPointMarker optimisé
const WarehouseMarker = React.memo(({ warehouse, returnTimes }) => {
  return (
    <Marker
      position={[warehouse.latitude, warehouse.longitude]}
      icon={icon} // Use the predefined icon
    >
      <Popup>
        <h1 className="popup-title"
        style= {{
          fontSize: '14px'
        }}
        >
          Warehouse
        </h1>
        <div className="popup-text">Return Times :</div>
        <div className="popup-list">
            {returnTimes.map((time, index) => (
                <div key={index} className="popup-item">
                  <div className="popup-dot"></div>
                  <div className="popup-name">Courier {index + 1} : {time}</div>
                </div>
            ))}
        </div>
      </Popup>
    </Marker>
  );
});

//Props Validation
WarehouseMarker.propTypes = {
  warehouse: PropTypes.shape({
    id: PropTypes.string,
    latitude: PropTypes.number,
    longitude: PropTypes.number,
  }),
  returnTimes: PropTypes.array,
};

//display name definition
WarehouseMarker.displayName = "WarehouseMarker";

export default WarehouseMarker;
