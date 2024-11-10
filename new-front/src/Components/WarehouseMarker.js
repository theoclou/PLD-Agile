import React from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";
import warehouseMarker from "../Assets/warehouseMarker.png";
import "./WarehouseMarker.css";

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
      icon={icon} // Utiliser l'icône prédéfinie
    >
      <Popup>
        Warehouse: {warehouse.id}
        <br />
        Latitude: {warehouse.latitude}
        <br />
        Longitude: {warehouse.longitude}
        <br />
        Return Times :
        <>
          <ul className="no-vertical-margin">
            {returnTimes.map((time, index) => (
              <li key={index}>
                Courier {index + 1}: {time}
              </li>
            ))}
          </ul>
        </>
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
