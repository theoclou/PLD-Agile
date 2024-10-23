import React from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";
import deliveryMarker from "../Assets/deliveryMarker.png";

const icon = L.icon({
  iconUrl: deliveryMarker,
  iconSize: [16, 16], // size of the icon
  iconAnchor: [8, 8], // point of the icon which will correspond to marker's location
  popupAnchor: [0, -10], // point from which the popup should open relative to the iconAnchor
});

// Composant DeliveryPointMarker optimisé
const DeliveryPointMarker = React.memo(({ delivery }) => {
  return (
    <Marker
      position={[
        delivery.deliveryAdress.latitude,
        delivery.deliveryAdress.longitude,
      ]}
      icon={icon} // Utiliser l'icône prédéfinie
    >
      <Popup>
        Intersection ID: {delivery.deliveryAdress.id}
        <br />
        Latitude: {delivery.deliveryAdress.latitude}
        <br />
        Longitude: {delivery.deliveryAdress.longitude}
        <br />
        Courier:{" "}
        {delivery.courier === null ? "Unassigned" : delivery.courier.id}
      </Popup>
    </Marker>
  );
});

//Props Validation
DeliveryPointMarker.propTypes = {
  delivery: PropTypes.shape({
    deliveryAdress: PropTypes.shape({
      id: PropTypes.number.isRequired,
      latitude: PropTypes.number.isRequired,
      longitude: PropTypes.number.isRequired,
    }).isRequired,
    courier: PropTypes.shape({
      id: PropTypes.number.isRequired,
    }).isRequired,
  }),
};

//display name definition
DeliveryPointMarker.displayName = "DeliveryPointMarker";

export default DeliveryPointMarker;
