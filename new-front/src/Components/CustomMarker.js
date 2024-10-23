import React from "react";
import { Marker, Popup } from "react-leaflet";
import L from "leaflet";

// Définir l'icône en dehors du composant pour éviter de la recréer à chaque rendu
const blackIcon = L.divIcon({
  className: "black-marker",
  html: '<div style="width: 8px; height: 8px; background-color: darkred; border-radius: 50%;"></div>',
  iconSize: [6, 6],
  iconAnchor: [3, 3],
  popupAnchor: [0, -10],
});

// Composant CustomMarker optimisé
const CustomMarker = React.memo(({ intersection }) => {
  return (
    <Marker
      position={[intersection.latitude, intersection.longitude]}
      icon={blackIcon} // Utiliser l'icône prédéfinie
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

export default CustomMarker;
