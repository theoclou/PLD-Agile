// MapDisplay.js
import React, { useMemo, useEffect } from "react";
import { MapContainer, TileLayer, Polyline, useMap } from "react-leaflet";
import MapMarker from "./MapMarker";
import DeliveryPointMarker from "./DeliveryPointMarker";
import WarehouseMarker from "./WarehouseMarker";

const MapDisplay = ({ mapData, deliveryData, bounds, zoom, setZoom }) => {
  const memoizedIntersections = useMemo(() => mapData.intersections, [mapData]);
  const memoizedSections = useMemo(() => mapData.sections, [mapData]);
  const memoizedDeliveries = useMemo(
    () => deliveryData.deliveries,
    [deliveryData]
  );
  const memoizedWarehouse = useMemo(
    () => deliveryData.warehouse,
    [deliveryData]
  );

  const ZoomListener = () => {
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
    }, [map]);

    return null;
  };

  const filteredIntersections = useMemo(() => {
    const minZoomForIntersections = 18; // Zoom threshold for displaying intersections

    if (zoom >= minZoomForIntersections) {
      // Remove the intersections that are already displayed as delivery points or as the warehouse
      return memoizedIntersections.filter((intersection) => {
        const isDeliveryPoint = memoizedDeliveries.some(
          (delivery) => delivery.deliveryAdress.id === intersection.id
        );
        const isWarehouse =
          memoizedWarehouse && memoizedWarehouse.id === intersection.id;
        return !isDeliveryPoint && !isWarehouse;
      });
    }
    return [];
  }, [zoom, memoizedIntersections, memoizedDeliveries]);

  return (
    <MapContainer
      bounds={
        bounds || [
          [48.8566, 2.3522],
          [48.8566, 2.3522],
        ]
      }
      zoom={zoom}
      className="map"
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      <ZoomListener />
      {filteredIntersections.map((intersection) => (
        <MapMarker key={intersection.id} intersection={intersection} />
      ))}
      {memoizedDeliveries.map((delivery) => (
        <DeliveryPointMarker key={delivery.id} delivery={delivery} />
      ))}
      {memoizedWarehouse && (
        <WarehouseMarker
          key={memoizedWarehouse.id}
          warehouse={memoizedWarehouse}
        />
      )}

      {memoizedSections.map((section, index) => {
        const originIntersection = section.origin;
        const destinationIntersection = section.destination;

        if (originIntersection && destinationIntersection) {
          const latLngs = [
            [originIntersection.latitude, originIntersection.longitude],
            [
              destinationIntersection.latitude,
              destinationIntersection.longitude,
            ],
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
