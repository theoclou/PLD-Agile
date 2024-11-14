import React, { useState, useRef, useEffect } from "react";
import PropTypes from "prop-types";
import "./TextSidebar.css";

const TextSidebar = React.memo(
  ({
    deliveryData,
    warehouse,
    sections,
    onDelete,
    highlightedDeliveryId,
    onMouseEnterDelivery,
    onMouseLeaveDelivery,
  }) => {
    const [expandedDeliveries, setExpandedDeliveries] = useState({});
    const deliveryRefs = useRef({});
    const sidebarRef = useRef(null);

    useEffect(() => {
      if (highlightedDeliveryId) {
        const delivery = deliveryData?.find(
          (d) => d.deliveryAdress.id === highlightedDeliveryId
        );

        if (delivery?.courier) {
          // Expand the courier's section
          setExpandedDeliveries((prev) => ({
            ...prev,
            [delivery.courier.id]: true,
          }));

          // Wait for DOM update before scrolling
          setTimeout(() => {
            const element = deliveryRefs.current[highlightedDeliveryId];
            if (element && sidebarRef.current) {
              const sidebarTop = sidebarRef.current.getBoundingClientRect().top;
              const elementTop = element.getBoundingClientRect().top;
              sidebarRef.current.scrollTop += elementTop - sidebarTop - 200;
            }
          }, 100);
        }
      }
    }, [highlightedDeliveryId, deliveryData]);

    const toggleDeliveryInfo = (courierId) => {
      setExpandedDeliveries((prevState) => ({
        ...prevState,
        [courierId]: !prevState[courierId],
      }));
    };

    const courierColors = {
      0: "#FF0000",
      1: "#0000FF",
      2: "#00FF00",
      3: "#FFA500",
      4: "#800080",
      5: "#FF1493",
      6: "#00FFFF",
      7: "#FFD700",
      8: "#463941",
      9: "#b95e1f",
    };

    const formatTime = (time) => {
      if (!time) return "Not scheduled";
      return `${String(time.hours).padStart(2, "0")}:${String(time.minutes).padStart(2, "0")}:${String(time.seconds).padStart(2, "0")}`;
    };

    const groupAndSortDeliveries = (deliveryData) => {
      // D'abord grouper par courier
      const grouped = deliveryData.reduce((acc, delivery) => {
        const courierId = delivery.courier ? delivery.courier.id : "unassigned";
        if (!acc[courierId]) {
          acc[courierId] = [];
        }
        acc[courierId].push(delivery);
        return acc;
      }, {});

      // Fonction pour convertir l'heure en minutes pour la comparaison
      const timeToMinutes = (time) => {
        if (!time) return Infinity;
        return time.hours * 3600 + time.minutes * 60 + time.seconds;
      };

      // Trier chaque groupe par heure d'arrivée
      Object.keys(grouped).forEach((courierId) => {
        if (courierId !== "unassigned" && Array.isArray(grouped[courierId])) {
          grouped[courierId].sort((a, b) => {
            const timeA = timeToMinutes(a.arrivalTime);
            const timeB = timeToMinutes(b.arrivalTime);
            return timeA - timeB;
          });
        }
      });

      return grouped;
    };

    if (!deliveryData || !sections || deliveryData.length === 0) {
      return (
        <div className="textual-sidebar">
          {warehouse && (
            <div className="under-section-container">
              <h2 className="section-title">Warehouse</h2>
              <div className="warehouse-section">
                <div className="section-info">
                  <h3 className="section-title">Warehouse #{warehouse.id}</h3>
                </div>
                <div className="section-info">
                  <span className="section-title">Sections around: </span>
                  <div className="section-list">
                    {(() => {
                      const relatedSections = sections.filter(
                        (section) =>
                          section.origin.id === warehouse.id.toString() ||
                          section.destination.id === warehouse.id.toString()
                      );

                      const uniqueSections = Array.from(
                        new Set(relatedSections.map((section) => section.name))
                      ).map((name) =>
                        relatedSections.find((section) => section.name === name)
                      );

                      const limitedSections = uniqueSections.slice(0, 2);

                      return limitedSections.length > 0 ? (
                        limitedSections.map((section, index) => (
                          <div key={index} className="section-item">
                            <div className="section-dot"></div>
                            <span className="section-name">
                              {section.name || "Undefined"}
                            </span>
                          </div>
                        ))
                      ) : (
                        <span className="no-sections">
                          No connected sections
                        </span>
                      );
                    })()}
                  </div>
                </div>
              </div>
            </div>
          )}
          <p className="text-gray-500">No delivery points available</p>
        </div>
      );
    }

    const groupedDeliveries = groupAndSortDeliveries(deliveryData);

    const renderDeliveryPoint = (delivery, courierId = null) => {
      const relatedSections = sections.filter(
        (section) =>
          section.origin.id === delivery.deliveryAdress.id.toString() ||
          section.destination.id === delivery.deliveryAdress.id.toString()
      );

      const uniqueSections = Array.from(
        new Set(relatedSections.map((section) => section.name))
      ).map((name) => relatedSections.find((section) => section.name === name));

      const limitedSections = uniqueSections.slice(0, 2);

      return (
        <div
          ref={(el) => (deliveryRefs.current[delivery.deliveryAdress.id] = el)}
          className="section-container"
          onMouseEnter={() => onMouseEnterDelivery(delivery.deliveryAdress.id)}
          onMouseLeave={onMouseLeaveDelivery}
          style={{
            backgroundColor:
              highlightedDeliveryId === delivery.deliveryAdress.id
                ? "rgb(255, 233, 233)"
                : "transparent",
            borderLeft: `4px solid ${courierId ? courierColors[courierId] : "#737373"}`,
            paddingLeft: "12px",
          }}
        >
          <div className="delivery-header">
            <h3 className="section-title">Delivery Point</h3>
            <div className="arrival-time">
              <span className="arrival-label">Arrival : </span>
              <span className="arrival-value">
                {formatTime(delivery.arrivalTime)}
              </span>
            </div>
          </div>

          <div className="warehouse-section">
            <div className="delivery-info">
              <div className="section-info">
                <span className="section-info">Courier ID: </span>
                <span
                  className="section-info"
                  style={{
                    color: courierId ? courierColors[courierId] : "inherit",
                  }}
                >
                  {courierId ? courierId : "Unassigned"}
                </span>
              </div>
            </div>

            <div>
              <span className="section-title">Sections around: </span>
              <div className="section-list">
                {limitedSections.length > 0 ? (
                  limitedSections.map((section, index) => (
                    <div key={index} className="section-item">
                      <div className="section-dot"></div>
                      <span className="section-name">
                        {section.name || "Undefined"}
                      </span>
                    </div>
                  ))
                ) : (
                  <span className="no-sections">No connected sections</span>
                )}
              </div>
            </div>
          </div>

          <div className="button-container">
            <button
              onClick={() => onDelete(delivery.deliveryAdress.id, courierId)}
              className="deletebutton"
            >
              &times;
            </button>
          </div>
        </div>
      );
    };

    return (
      <div className="textual-sidebar" ref={sidebarRef}>
        {warehouse && (
          <div className="warehouse-container">
            <h2 className="section-title">Warehouse</h2>
            <div className="section-container">
              <div className="section-info">
                <h3 className="section-title">Warehouse</h3>
              </div>
              <div className="section-info">
                <span className="section-title">Sections around: </span>
                <div className="section-list">
                  {(() => {
                    const relatedSections = sections.filter(
                      (section) =>
                        section.origin.id === warehouse.id.toString() ||
                        section.destination.id === warehouse.id.toString()
                    );
                    const uniqueSections = Array.from(
                      new Set(relatedSections.map((section) => section.name))
                    ).map((name) =>
                      relatedSections.find((section) => section.name === name)
                    );
                    const limitedSections = uniqueSections.slice(0, 2);
                    return limitedSections.length > 0 ? (
                      limitedSections.map((section, index) => (
                        <div key={index} className="section-item">
                          <div className="section-dot"></div>
                          <span className="section-name">
                            {section.name || "Undefined"}
                          </span>
                        </div>
                      ))
                    ) : (
                      <span className="no-sections">No connected sections</span>
                    );
                  })()}
                </div>
              </div>
            </div>
          </div>
        )}

        {groupedDeliveries.unassigned?.length > 0 && (
          <div>
            <h2 className="section-title">Unassigned Delivery Points</h2>
            {groupedDeliveries.unassigned.map((delivery) =>
              renderDeliveryPoint(delivery)
            )}
          </div>
        )}

        {Object.entries(groupedDeliveries)
          .filter(([courierId]) => courierId !== "unassigned")
          .map(([courierId, deliveries]) => (
            <div key={courierId}>
              <h2
                className="section-title"
                style={{ color: courierColors[courierId] }}
              >
                Courier {courierId} Delivery Points
                <button
                  onClick={() => toggleDeliveryInfo(courierId)}
                  className="toggle-button"
                >
                  {expandedDeliveries[courierId] ? "⏶" : "⏷"}
                </button>
              </h2>
              {expandedDeliveries[courierId] &&
                deliveries.map((delivery) =>
                  renderDeliveryPoint(delivery, courierId)
                )}
            </div>
          ))}
      </div>
    );
  }
);

TextSidebar.propTypes = {
  deliveryData: PropTypes.arrayOf(
    PropTypes.shape({
      deliveryAdress: PropTypes.shape({
        id: PropTypes.string.isRequired,
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,
      }).isRequired,
      courier: PropTypes.shape({
        id: PropTypes.number,
      }),
      arrivalTime: PropTypes.shape({
        hours: PropTypes.number,
        minutes: PropTypes.number,
      }),
    })
  ),
  sections: PropTypes.arrayOf(
    PropTypes.shape({
      destination: PropTypes.shape({
        id: PropTypes.string.isRequired,
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,
      }).isRequired,
      origin: PropTypes.shape({
        id: PropTypes.string.isRequired,
        latitude: PropTypes.number.isRequired,
        longitude: PropTypes.number.isRequired,
      }).isRequired,
      length: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
    })
  ),
  onDelete: PropTypes.func.isRequired,
  highlightedDeliveryId: PropTypes.string,
  onMouseEnterDelivery: PropTypes.func.isRequired,
  onMouseLeaveDelivery: PropTypes.func.isRequired,
};

export default TextSidebar;
