// src/components/ArrowedPolyline.js
import React, { useEffect } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-polylinedecorator";
import PropTypes from "prop-types";

const ArrowedPolyline = React.memo(
  ({ positions, color, weight = 3, opacity = 1 }) => {
    const map = useMap();

    useEffect(() => {
      const polyline = L.polyline(positions, {
        color,
        weight,
        opacity,
      });

      // Fonction pour ajuster la taille des flèches selon le zoom
      const getArrowSize = () => {
        const zoom = map.getZoom();
        // Ajustez ces valeurs selon vos besoins
        if (zoom <= 12) return 6;
        if (zoom <= 14) return 8;
        if (zoom <= 16) return 10;
        return 12;
      };

      // Fonction pour ajuster l'espacement des flèches selon le zoom
      const getArrowSpacing = () => {
        const zoom = map.getZoom();
        // Ajustez ces valeurs selon vos besoins
        if (zoom <= 12) return 100;
        if (zoom <= 14) return 80;
        if (zoom <= 16) return 60;
        return 50;
      };

      const updateDecorator = () => {
        if (decorator) {
          map.removeLayer(decorator);
        }

        decorator = L.polylineDecorator(polyline, {
          patterns: [
            {
              offset: "25%",
              repeat: getArrowSpacing(),
              symbol: L.Symbol.arrowHead({
                pixelSize: getArrowSize(),
                polygon: true,
                pathOptions: {
                  fillOpacity: opacity,
                  weight: 0,
                  color,
                  fillColor: color,
                },
              }),
            },
          ],
        });

        decorator.addTo(map);
      };

      let decorator = L.polylineDecorator(polyline, {
        patterns: [
          {
            offset: "25%",
            repeat: getArrowSpacing(),
            symbol: L.Symbol.arrowHead({
              pixelSize: getArrowSize(),
              polygon: true,
              pathOptions: {
                fillOpacity: opacity,
                weight: 0,
                color,
                fillColor: color,
              },
            }),
          },
        ],
      });

      polyline.addTo(map);
      decorator.addTo(map);

      // Mettre à jour les flèches quand le zoom change
      map.on("zoomend", updateDecorator);

      return () => {
        map.removeLayer(polyline);
        map.removeLayer(decorator);
        map.off("zoomend", updateDecorator);
      };
    }, [map, positions, color, weight, opacity]);

    return null;
  }
);

ArrowedPolyline.propTypes = {
  positions: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.number)).isRequired,
  color: PropTypes.string.isRequired,
  weight: PropTypes.number,
  opacity: PropTypes.number,
};

export default ArrowedPolyline;
