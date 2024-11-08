import React, {
  useMemo,
  useEffect,
  useCallback,
  useState,
  useRef,
} from "react";
import { MapContainer, TileLayer, Polyline, useMap } from "react-leaflet";
import MapMarker from "./MapMarker";
import DeliveryPointMarker from "./DeliveryPointMarker";
import WarehouseMarker from "./WarehouseMarker";

const MIN_ZOOM_FOR_INTERSECTIONS = 18;

// Définition des couleurs pour les coursiers avec une meilleure visibilité
const COURIER_COLORS = {
  0: "#FF0000", // Rouge pour le coursier 0
  1: "#0000FF", // Bleu pour le coursier 1
  2: "#00FF00", // Vert pour le coursier 2
  3: "#FFA500", // Orange pour le coursier 3
  4: "#800080", // Violet pour le coursier 4
  5: "#FF1493", // Rose foncé pour le coursier 5
  6: "#00FFFF", // Cyan pour le coursier 6
  7: "#FFD700", // Or pour le coursier 7
};

// Couleur par défaut pour les sections non assignées
const DEFAULT_SECTION_COLOR = "darkgrey";

const MapController = React.memo(({ bounds }) => {
  const map = useMap();

  useEffect(() => {
    if (bounds) {
      map.fitBounds(bounds);
      const timeoutId = setTimeout(() => {
        map.invalidateSize();
      }, 100);
      return () => clearTimeout(timeoutId);
    }
  }, [bounds, map]);

  return null;
});

const ZoomListener = React.memo(({ setZoom }) => {
  const map = useMap();

  useEffect(() => {
    const handleZoomEnd = () => {
      setZoom(map.getZoom());
    };

    map.on("zoomend", handleZoomEnd);
    return () => map.off("zoomend", handleZoomEnd);
  }, [map, setZoom]);

  return null;
});

const MapDisplay = ({
  mapData,
  deliveryData,
  bounds,
  zoom,
  setZoom,
  onIntersectionClick,
  addingDeliveryPoint,
  highlightedDeliveryId,
  onMouseEnterDelivery,
  onMouseLeaveDelivery,
  routes = [],
}) => {
  const routesRef = useRef(routes);
  const [forceUpdate, setForceUpdate] = useState(0);

  useEffect(() => {
    if (JSON.stringify(routesRef.current) !== JSON.stringify(routes)) {
      routesRef.current = routes;
      setForceUpdate((prev) => prev + 1);
    }
  }, [routes]);

  const memoizedIntersections = useMemo(
    () => mapData.intersections || [],
    [mapData]
  );
  const memoizedSections = useMemo(() => mapData.sections || [], [mapData]);
  const memoizedDeliveries = useMemo(
    () => deliveryData.deliveries || [],
    [deliveryData]
  );
  const memoizedWarehouse = useMemo(
    () => deliveryData.warehouse,
    [deliveryData]
  );

  // Nouvelle fonction pour trouver la route et le coursier associé à une section
  const findRouteAndCourier = useCallback(
    (origin, destination) => {
      const sectionKey = `${origin.id}-${destination.id}`;
      const reverseSectionKey = `${destination.id}-${origin.id}`;

      for (const route of routes) {
        const path = route.path;
        for (let i = 0; i < path.length - 1; i++) {
          const currentKey = `${path[i].id}-${path[i + 1].id}`;
          const reverseKey = `${path[i + 1].id}-${path[i].id}`;

          if (
            currentKey === sectionKey ||
            currentKey === reverseSectionKey ||
            reverseKey === sectionKey ||
            reverseKey === reverseSectionKey
          ) {
            return {
              found: true,
              courierId: route.courierId,
            };
          }
        }
      }

      return {
        found: false,
        courierId: null,
      };
    },
    [routes]
  );

  const filteredIntersections = useMemo(() => {
    if (zoom < MIN_ZOOM_FOR_INTERSECTIONS) return [];

    return memoizedIntersections.filter((intersection) => {
      if (!intersection?.id) return false;

      const isDeliveryPoint = memoizedDeliveries.some(
        (delivery) => delivery?.deliveryAdress?.id === intersection.id
      );
      const isWarehouse = memoizedWarehouse?.id === intersection.id;

      return !isDeliveryPoint && !isWarehouse;
    });
  }, [zoom, memoizedIntersections, memoizedDeliveries, memoizedWarehouse]);

  const center = useMemo(() => {
    if (memoizedIntersections.length === 0) return [48.8566, 2.3522];

    const latitudes = memoizedIntersections.map((i) => i.latitude);
    const longitudes = memoizedIntersections.map((i) => i.longitude);

    return [
      (Math.min(...latitudes) + Math.max(...latitudes)) / 2,
      (Math.min(...longitudes) + Math.max(...longitudes)) / 2,
    ];
  }, [memoizedIntersections]);

  const renderedSections = useMemo(() => {
    return memoizedSections.map((section, index) => {
      const { origin, destination } = section;
      if (!origin?.latitude || !destination?.latitude) return null;

      const latLngs = [
        [origin.latitude, origin.longitude],
        [destination.latitude, destination.longitude],
      ];

      const { found, courierId } = findRouteAndCourier(origin, destination);

      return (
        <Polyline
          key={`${index}-${forceUpdate}`}
          positions={latLngs}
          color={
            found
              ? COURIER_COLORS[courierId] || DEFAULT_SECTION_COLOR
              : DEFAULT_SECTION_COLOR
          }
          weight={found ? 3 : 2}
          opacity={1}
        />
      );
    });
  }, [memoizedSections, findRouteAndCourier, forceUpdate]);

  return (
    <MapContainer center={center} bounds={bounds} zoom={zoom} className="map">
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      />

      <MapController bounds={bounds} />
      <ZoomListener setZoom={setZoom} />

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
            highlighted={highlightedDeliveryId === delivery.deliveryAdress.id}
            onMouseEnter={onMouseEnterDelivery}
            onMouseLeave={onMouseLeaveDelivery}
          />
        ) : null
      )}

      {memoizedWarehouse && (
        <WarehouseMarker
          key={memoizedWarehouse.id}
          warehouse={memoizedWarehouse}
        />
      )}

      {renderedSections}
    </MapContainer>
  );
};

export default React.memo(MapDisplay);
