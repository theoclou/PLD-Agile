import React, { useEffect, useRef } from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";
import deliveryMarker from "../Assets/deliveryMarker.png";

// Composant DeliveryPointMarker optimisé
const DeliveryPointMarker = React.memo(({ delivery, highlighted, onMouseEnter, onMouseLeave }) => {
  const markerRef = useRef();

  const icon = L.icon({
    iconUrl: deliveryMarker,
    iconSize: highlighted ? [40, 40] : [30, 30],
    iconAnchor: [15, 15],
    popupAnchor: [0, -10],
  });

  useEffect(() => {
    const marker = markerRef.current;
    if (marker) {
      // Ajouter les écouteurs d'événements directement sur l'instance du marker
      marker.on("mouseover", () => onMouseEnter(delivery.deliveryAdress.id));
      marker.on("mouseout", onMouseLeave);

      // Nettoyer les écouteurs d'événements quand le composant est démonté
      return () => {
        marker.off("mouseover");
        marker.off("mouseout");
      };
    }
  }, [onMouseEnter, onMouseLeave, delivery.deliveryAdress.id]);

  return (
      <Marker
          ref={markerRef} // Utiliser ref directement ici
          position={[delivery.deliveryAdress.latitude, delivery.deliveryAdress.longitude]}
          icon={icon}
      >
        <Popup>
          Intersection ID: {delivery.deliveryAdress.id}
          <br />
          Latitude: {delivery.deliveryAdress.latitude}
          <br />
          Longitude: {delivery.deliveryAdress.longitude}
          <br />
          Courier: {delivery.courier === null ? "Unassigned" : delivery.courier.id}
        </Popup>
      </Marker>
  );
});

DeliveryPointMarker.propTypes = {
  delivery: PropTypes.shape({
    deliveryAdress: PropTypes.shape({
      id: PropTypes.number.isRequired,
      latitude: PropTypes.number.isRequired,
      longitude: PropTypes.number.isRequired,
    }).isRequired,
    courier: PropTypes.shape({
      id: PropTypes.number,
    }),
  }).isRequired,
  highlighted: PropTypes.bool,
  onMouseEnter: PropTypes.func.isRequired,
  onMouseLeave: PropTypes.func.isRequired,
};

export default DeliveryPointMarker;
