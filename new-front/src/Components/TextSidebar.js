import React from "react";
import PropTypes from "prop-types";

const TextSidebar = React.memo(({ deliveryData, sections }) => {
  if (!deliveryData || !sections || deliveryData.length === 0) {
    return (
      <div className="p-4">
        <p className="text-gray-500">No delivery points available</p>
      </div>
    );
  }
  console.log(deliveryData);
  console.log(sections);

  return (
    <div className="p-4 bg-white shadow-sm overflow-y-auto max-h-screen">
      <h2 className="text-lg font-semibold mb-4">Delivery Points</h2>

      <div className="space-y-4">
        {deliveryData.map((delivery) => {
          // Find sections that contain this delivery point
          const relatedSections = sections.filter(
            (section) =>
              section.origin.id === delivery.deliveryAdress.id.toString() ||
              section.destination.id === delivery.deliveryAdress.id.toString()
          );

          return (
            <div
              key={delivery.deliveryAdress.id}
              className="border rounded-lg p-4 bg-gray-50"
            >
              <h3 className="font-medium text-gray-900">
                Delivery Point #{delivery.deliveryAdress.id}
              </h3>

              <div className="mt-2 space-y-2">
                <div className="text-sm">
                  <span className="font-medium text-gray-600">Location: </span>
                  <span className="text-gray-700">
                    {delivery.deliveryAdress.latitude.toFixed(4)}°,
                    {delivery.deliveryAdress.longitude.toFixed(4)}°
                  </span>
                </div>

                <div className="text-sm">
                  <span className="font-medium text-gray-600">
                    Courier ID:{" "}
                  </span>
                  <span className="text-gray-700">
                    {delivery.courier === null
                      ? "Unassigned"
                      : delivery.courier.id}
                  </span>
                </div>

                <div className="text-sm">
                  <span className="font-medium text-gray-600">Sections: </span>
                  <div className="ml-2 mt-1">
                    {relatedSections.length > 0 ? (
                      relatedSections.map((section, index) => (
                        <div key={index} className="mb-2">
                          <div className="flex items-center space-x-2">
                            <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                            <span className="text-gray-700">
                              {section.name}
                            </span>
                          </div>
                          <div className="text-xs text-gray-500 ml-4">
                            Length: {section.length.toFixed(2)} meters
                          </div>
                        </div>
                      ))
                    ) : (
                      <span className="text-gray-500">
                        No connected sections
                      </span>
                    )}
                  </div>
                </div>
              </div>
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
};

TextSidebar.displayName = "TextSidebar";

export default TextSidebar;
