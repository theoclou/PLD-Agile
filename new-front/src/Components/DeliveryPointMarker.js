import React, { useEffect, useRef } from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";
import PropTypes from "prop-types";
import deliveryMarker from "../Assets/deliveryMarker.png";

// Optimized DeliveryPointMarker component
const DeliveryPointMarker = React.memo(
  ({ delivery, highlighted, onMouseEnter, onMouseLeave }) => {
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
        // Add event listeners directly on the marker instance
        marker.on("mouseover", () => onMouseEnter(delivery.deliveryAdress.id));
        marker.on("mouseout", onMouseLeave);

        // Clean the event listeners when the component is destroyed
        return () => {
          marker.off("mouseover");
          marker.off("mouseout");
        };
      }
    }, [onMouseEnter, onMouseLeave, delivery.deliveryAdress.id]);

    return (
      <Marker
        ref={markerRef}
        position={[
          delivery.deliveryAdress.latitude,
          delivery.deliveryAdress.longitude,
        ]}
        icon={icon}
      >
        <Popup>
          <div className="popup-text">
            Courier :{" "}
            {delivery.courier === null ? "Unassigned" : delivery.courier.id}
          <br />
          {delivery.arrivalTime === null
            ? ""
            : "Arrival time : " + delivery.arrivalTime}
          </div>
        </Popup>
      </Marker>
    );
  }
);

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
    arrivalTime: PropTypes.shape({
      hours: PropTypes.number.isRequired,
      minutes: PropTypes.number.isRequired,
    }),
  }).isRequired,
  highlighted: PropTypes.bool,
  onMouseEnter: PropTypes.func.isRequired,
  onMouseLeave: PropTypes.func.isRequired,
};

export default DeliveryPointMarker;
