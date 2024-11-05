import React, { useMemo, useEffect, useCallback } from "react";
import { MapContainer, TileLayer, Polyline, useMap } from "react-leaflet";
import MapMarker from "./MapMarker";
import DeliveryPointMarker from "./DeliveryPointMarker";
import WarehouseMarker from "./WarehouseMarker";

const MIN_ZOOM_FOR_INTERSECTIONS = 18;

const MapController = ({ bounds }) => {
  const map = useMap();

  useEffect(() => {
    if (bounds) {
      map.fitBounds(bounds);
      setTimeout(() => {
        map.invalidateSize();
      }, 100);
    }
  }, [bounds, map]);

  return null;
};

const ZoomListener = ({ setZoom }) => {
  const map = useMap();

  useEffect(() => {
    const handleZoomEnd = () => {
      const currentZoom = map.getZoom();
      setZoom(currentZoom);
    };

    map.on("zoomend", handleZoomEnd);
    return () => {
      map.off("zoomend", handleZoomEnd);
    };
  }, [map, setZoom]);

  return null;
};

const MapDisplay = ({
                      mapData,
                      deliveryData,
                      bounds,
                      zoom,
                      setZoom,
                      onIntersectionClick,
                      addingDeliveryPoint
                    }) => {
  const memoizedIntersections = useMemo(() => mapData.intersections || [], [mapData]);
  const memoizedSections = useMemo(() => mapData.sections || [], [mapData]);
  const memoizedDeliveries = useMemo(() => deliveryData.deliveries || [], [deliveryData]);
  const memoizedWarehouse = useMemo(() => deliveryData.warehouse, [deliveryData]);

  // Filtrer les intersections basé sur le zoom et exclure les points de livraison et l'entrepôt
  const filteredIntersections = useMemo(() => {
    if (zoom >= MIN_ZOOM_FOR_INTERSECTIONS) {
      return memoizedIntersections.filter(intersection => {
        if (!intersection || !intersection.id) return false;

        // Exclure les points qui sont des livraisons
        const isDeliveryPoint = memoizedDeliveries.some(
          delivery => delivery?.deliveryAdress?.id === intersection.id
        );

        // Exclure le point qui est l'entrepôt
        const isWarehouse = memoizedWarehouse &&
          memoizedWarehouse.id === intersection.id;

        return !isDeliveryPoint && !isWarehouse;
      });
    }
    return [];
  }, [zoom, memoizedIntersections, memoizedDeliveries, memoizedWarehouse]);

  const calculateCenter = useCallback(() => {
    if (memoizedIntersections.length > 0) {
      const latitudes = memoizedIntersections.map(i => i.latitude);
      const longitudes = memoizedIntersections.map(i => i.longitude);
      return [
        (Math.min(...latitudes) + Math.max(...latitudes)) / 2,
        (Math.min(...longitudes) + Math.max(...longitudes)) / 2
      ];
    }
    return [48.8566, 2.3522]; // Paris par défaut
  }, [memoizedIntersections]);

  const center = useMemo(() => calculateCenter(), [calculateCenter]);

  return (
    <MapContainer
      center={center}
      bounds={bounds}
      zoom={zoom}
      className="map"
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />

      <MapController bounds={bounds} />
      <ZoomListener setZoom={setZoom} />

      {/* high zoom */}
      {filteredIntersections.map((intersection) => (
        <MapMarker
          key={intersection.id}
          intersection={intersection}
          onIntersectionClick={addingDeliveryPoint ? onIntersectionClick : null}
        />
      ))}

      {memoizedDeliveries.map((delivery) =>
        delivery?.deliveryAdress ? (
          <DeliveryPointMarker
            key={delivery.deliveryAdress.id}
            delivery={delivery}
          />
        ) : null
      )}

      {memoizedWarehouse && (
        <WarehouseMarker
          key={memoizedWarehouse.id}
          warehouse={memoizedWarehouse}
        />
      )}

      {memoizedSections.map((section, index) => {
        const originIntersection = section.origin;
        const destinationIntersection = section.destination;

        if (originIntersection?.latitude && destinationIntersection?.latitude) {
          const latLngs = [
            [originIntersection.latitude, originIntersection.longitude],
            [destinationIntersection.latitude, destinationIntersection.longitude],
          ];

          return (
            <Polyline
              key={index}
              positions={latLngs}
              color="darkgrey"
              weight={2}
              opacity={1}
            />
          );
        }
        return null;
      })}
    </MapContainer>
  );
};

export default MapDisplay;