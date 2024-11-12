import React, {useState} from "react";
import PropTypes from "prop-types";
import "./TextSidebar.css"

const TextSidebar = React.memo(({ deliveryData, warehouse, sections, onDelete, highlightedDeliveryId, onMouseEnterDelivery,onMouseLeaveDelivery }) => {
  const [expandedDeliveries, setExpandedDeliveries] = useState({}); // State to manage the display of delivery points data

  // Function to switch the delivery point details display
  const toggleDeliveryInfo = (courierId) => {
    setExpandedDeliveries((prevState) => ({
      ...prevState,
      [courierId]: !prevState[courierId], // reverse the state
    }));
  };

  // Definition of the courier's colors
  const courierColors = {
    0: "#FF0000",  // Rouge
    1: "#0000FF",  // Bleu
    2: "#00FF00",  // Vert
    3: "#FFA500",  // Orange
    4: "#800080",  // Violet
    5: "#FF1493",  // Rose
    6: "#00FFFF",  // Cyan
    7: "#FFD700"   // Or
  };

  // Check if no delivery data is available
  if (!deliveryData || !sections || deliveryData.length === 0) {
    return (
      <div className="textual-sidebar">
        {/* Display the warehouse information even if no delivery points are available */}
        {warehouse && (
          <div className="under-section-container">
            <h2 className="section-title">Warehouse</h2>
            <div className="warehouse-section">
              <div className="section-info">
                <h3 className="section-title">
                  Warehouse #{warehouse.id}
                </h3>
              </div>
              {/* Display sections related to the warehouse */}
              <div className="section-info">
                <span className="section-title">Sections around: </span>
                <div className="section-list">
                  {(() => {
                    const relatedSections = sections.filter(section =>
                      section.origin.id === warehouse.id.toString() ||
                      section.destination.id === warehouse.id.toString()
                    );

                    // Remove duplicate sections
                    const uniqueSections = Array.from(
                      new Set(relatedSections.map(section => section.name))
                    ).map(name => relatedSections.find(section => section.name === name));

                    // Limit to a maximum of 2 sections
                    const limitedSections = uniqueSections.slice(0, 2);

                    return limitedSections.length > 0 ? (
                      limitedSections.map((section, index) => (
                        <div key={index} className="section-item">
                          <div className="section-dot"> </div>
                          <span className="section-name">{section.name}</span>
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

        {/* Message indicating no delivery points are available */}
        <p className="text-gray-500">No delivery points available</p>
      </div>
    );
  }

  // Group the delivery routs by couriers
  const groupedDeliveries = deliveryData.reduce((acc, delivery) => {
    const courierId = delivery.courier ? delivery.courier.id : 'unassigned';
    if (!acc[courierId]) {
      acc[courierId] = [];
    }
    acc[courierId].push(delivery);
    return acc;
  }, {});

  return (
    <div className="textual-sidebar">
      {/* Displaying warehouse information */}
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
                  const relatedSections = sections.filter(section =>
                    section.origin.id === warehouse.id.toString() ||
                    section.destination.id === warehouse.id.toString()
                  );

                  // Remove duplicate sections
                  const uniqueSections = Array.from(
                    new Set(relatedSections.map(section => section.name))
                  ).map(name => relatedSections.find(section => section.name === name));

                  // Limit to a maximum of 2 sections
                  const limitedSections = uniqueSections.slice(0, 2);

                  return limitedSections.length > 0 ? (
                    limitedSections.map((section, index) => (
                      <div key={index} className="section-item">
                        <div className="section-dot"> </div>
                        <span className="section-name">{section.name}</span>
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

      {/* Non Assigned points */}
      {groupedDeliveries.unassigned && groupedDeliveries.unassigned.length > 0 && (
        <div>
          <h2 className="section-title">Unassigned Delivery Points</h2>
          {groupedDeliveries.unassigned.map((delivery) => {
            const relatedSections = sections.filter(
              (section) =>
                section.origin.id === delivery.deliveryAdress.id.toString() ||
                section.destination.id === delivery.deliveryAdress.id.toString()
            );

            const uniqueSections = Array.from(
              new Set(relatedSections.map((section) => section.name))
            ).map((name) =>
              relatedSections.find((section) => section.name === name)
            );

            const limitedSections = uniqueSections.slice(0, 2);

            return (
              <div className="section-container" key={delivery.deliveryAdress.id}>
                <div
                  onMouseEnter={() => onMouseEnterDelivery(delivery.deliveryAdress.id)}
                  onMouseLeave={onMouseLeaveDelivery}
                  className={`delivery-item ${highlightedDeliveryId === delivery.deliveryAdress.id ? "highlighted" : ""}`}
                  style={{
                    backgroundColor: highlightedDeliveryId === delivery.deliveryAdress.id ? 'rgb(255, 233, 233)' : 'transparent',
                    borderLeft: '4px solid #737373',
                    paddingLeft: '12px'
                  }}
                >
                  <h3 className="section-title">
                    Delivery Point
                  </h3>

                  <div className="warehouse-section">
                    <div>
                      <span className="section-info">Courier ID : </span>
                      <span className="section-info">Unassigned</span>
                    </div>

                    <div>
                      <span className="section-title">Sections around: </span>
                      <div className="section-list">
                        {limitedSections.length > 0 ? (
                          limitedSections.map((section, index) => (
                            <div key={index} className="section-item">
                              <div className="section-dot"></div>
                              <span className="section-name">{section.name}</span>
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
                      onClick={() => {
                        console.log("Delete button clicked for ID:", delivery.deliveryAdress.id);
                        onDelete(delivery.deliveryAdress.id);
                      }}
                      className="deletebutton"
                    >
                      &times;
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Assigned points, grouped by couriers */}
      {Object.entries(groupedDeliveries)
        .filter(([courierId]) => courierId !== 'unassigned')
        .map(([courierId, deliveries]) => (
          <div key={courierId}>
            <h2 className="section-title" style={{ color: courierColors[courierId] }}>
              Courier {courierId} Delivery Points
              {/* Button to display/hide information */}
              <button
                  onClick={() => toggleDeliveryInfo(courierId)}
                  className="toggle-button"
              >
                {expandedDeliveries[courierId] ? '⏶' : '⏷'}
              </button>
            </h2>



            {/* Display the information if extandedDeliveries[courierId] is true */}
            {expandedDeliveries[courierId] && deliveries.map((delivery) => {
              const relatedSections = sections.filter(
                (section) =>
                  section.origin.id === delivery.deliveryAdress.id.toString() ||
                  section.destination.id === delivery.deliveryAdress.id.toString()
              );

              const uniqueSections = Array.from(
                new Set(relatedSections.map((section) => section.name))
              ).map((name) =>
                relatedSections.find((section) => section.name === name)
              );

              const limitedSections = uniqueSections.slice(0, 2);

              return (
                <div className="section-container" key={delivery.deliveryAdress.id}>
                  <div
                    onMouseEnter={() => onMouseEnterDelivery(delivery.deliveryAdress.id)}
                    onMouseLeave={onMouseLeaveDelivery}
                    className={`delivery-item ${highlightedDeliveryId === delivery.deliveryAdress.id ? "highlighted" : ""}`}
                    style={{
                      backgroundColor: highlightedDeliveryId === delivery.deliveryAdress.id ? 'rgb(255, 233, 233)' : 'transparent',
                      borderLeft: `4px solid ${courierColors[courierId]}`,
                      paddingLeft: '12px'
                    }}
                  >
                    <h3 className="section-title">
                      Delivery Point
                    </h3>

                    <div className="warehouse-section">
                      <div>
                        <span className="section-info">Courier ID: </span>
                        <span className="section-info" style={{ color: courierColors[courierId] }}>
                          {delivery.courier.id}
                        </span>
                      </div>

                      <div>
                        <span className="section-title">Sections around: </span>
                        <div className="section-list">
                          {limitedSections.length > 0 ? (
                            limitedSections.map((section, index) => (
                              <div key={index} className="section-item">
                                <div className="section-dot"></div>
                                <span className="section-name">{section.name}</span>
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
                        onClick={() => onDelete(delivery.deliveryAdress.id)}
                        className="deletebutton"
                      >
                        &times;
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ))}
    </div>
  );
});

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
  onMouseLeaveDelivery: PropTypes.func.isRequired
};

TextSidebar.displayName = "TextSidebar";

export default TextSidebar;