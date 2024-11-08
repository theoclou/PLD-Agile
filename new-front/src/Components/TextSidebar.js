import React from "react";
import PropTypes from "prop-types";
import "./TextSidebar.css"

const TextSidebar = React.memo(({ deliveryData, warehouse, sections, onDelete, highlightedDeliveryId, onMouseEnterDelivery,onMouseLeaveDelivery }) => {
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

  console.log(deliveryData);
  console.log(sections);
  console.log("onDelete function received:", onDelete);

  return (
    <div className="textual-sidebar">
      {/* Displaying warehouse information */}
      {warehouse && (
        <div className="warehouse-container">
          <h2 className="section-title">Warehouse</h2>
          <div className="section-container">
            <div className="section-info">
              <h3 className="section-title">Warehouse #{warehouse.id}</h3>
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

      <h2 className="section-title">Delivery Points</h2>
        <div className="section-container">
            {deliveryData.map((delivery) => {
                // Find sections that contain this delivery point
                const relatedSections = sections.filter(
                    (section) =>
                        section.origin.id === delivery.deliveryAdress.id.toString() ||
                        section.destination.id === delivery.deliveryAdress.id.toString()
                );

                // Remove duplicate sections
                const uniqueSections = Array.from(
                    new Set(relatedSections.map((section) => section.name))
                ).map((name) =>
                    relatedSections.find((section) => section.name === name)
                );

                // Limit to a maximum of 2 sections
                const limitedSections = uniqueSections.slice(0, 2);

                return (


                    <div key={delivery.deliveryAdress.id}
                         onMouseEnter={() => onMouseEnterDelivery(delivery.deliveryAdress.id)}
                         onMouseLeave={onMouseLeaveDelivery}
                         className={`delivery-item ${highlightedDeliveryId === delivery.deliveryAdress.id ? "highlighted" : ""}`}
                         style={{backgroundColor: highlightedDeliveryId === delivery.deliveryAdress.id ? 'yellow' : 'transparent'}}>

                        <h3 className="section-title">
                            Delivery Point #{delivery.deliveryAdress.id}
                        </h3>

                        <div className="warehouse-section">
                            <div className="text-sm">
                                <span className="section-info">Courier ID: </span>
                                <span className="section-info">
                    {delivery.courier === null ? "Unassigned" : delivery.courier.id}
                  </span>
                            </div>

                            <div className="text-sm">
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
                                        <span className="text-gray-500">No connected sections</span>
                                    )}
                                </div>
                            </div>
                        </div>
                        <button
                            onClick={() => {
                                console.log("Delete button clicked for ID:", delivery.deliveryAdress.id);
                                onDelete(delivery.deliveryAdress.id);
                            }}
                            className="text-red-500 hover:text-red-700"
                        >
                            &times; {/* Button to delete the delivery point */}
                        </button>
                    </div>
                );
            })}
        </div>
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
        id: PropTypes.number.isRequired,
      }).isRequired,
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
};

TextSidebar.displayName = "TextSidebar";

export default TextSidebar;