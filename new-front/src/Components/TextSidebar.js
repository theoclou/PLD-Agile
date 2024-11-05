import React from "react";
import PropTypes from "prop-types";

const TextSidebar = React.memo(({ deliveryData, warehouse, sections, onDelete }) => {
    // Check if no delivery data is available
    if (!deliveryData || !sections || deliveryData.length === 0) {
        return (
            <div className="p-4 bg-white shadow-sm overflow-y-auto max-h-screen">
                {/* Display the warehouse information even if no delivery points are available */}
                {warehouse && (
                    <div className="border rounded-lg p-4 bg-gray-50 mb-4">
                        <h2 className="font-medium text-gray-900">Warehouse</h2>
                        <div className="mt-2 space-y-2">
                            <div className="text-sm">
                                <h3 className="font-medium text-gray-900">
                                    Warehouse #{warehouse.id}
                                </h3>
                            </div>
                            {/* Display sections related to the warehouse */}
                            <div className="text-sm">
                                <span className="font-medium text-gray-600">Sections around: </span>
                                <div className="ml-2 mt-1">
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
                                                <div key={index} className="mb-2">
                                                    <div className="flex items-center space-x-2">
                                                        <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                                                        <span className="text-gray-700">{section.name}</span>
                                                    </div>
                                                </div>
                                            ))
                                        ) : (
                                            <span className="text-gray-500">No connected sections</span>
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
        <div className="p-4 bg-white shadow-sm overflow-y-auto max-h-screen">
            {/* Displaying warehouse information */}
            {warehouse && (
                <div className="border rounded-lg p-4 bg-gray-50 mb-4">
                    <h2 className="font-medium text-gray-900">Warehouse</h2>
                    <div className="mt-2 space-y-2">
                        <div className="text-sm">
                            <h3 className="font-medium text-gray-900">Warehouse #{warehouse.id}</h3>
                        </div>
                        <div className="text-sm">
                            <span className="font-medium text-gray-600">Sections around: </span>
                            <div className="ml-2 mt-1">
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
                                            <div key={index} className="mb-2">
                                                <div className="flex items-center space-x-2">
                                                    <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                                                    <span className="text-gray-700">{section.name}</span>
                                                </div>
                                            </div>
                                        ))
                                    ) : (
                                        <span className="text-gray-500">No connected sections</span>
                                    );
                                })()}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <h2 className="text-lg font-semibold mb-4">Delivery Points</h2>
            <div className="space-y-4">
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
                        <div key={delivery.deliveryAdress.id} className="border rounded-lg p-4 bg-gray-50">
                            <h3 className="font-medium text-gray-900">
                                Delivery Point #{delivery.deliveryAdress.id}
                            </h3>

                            <div className="mt-2 space-y-2">
                                <div className="text-sm">
                                    <span className="font-medium text-gray-600">Courier ID: </span>
                                    <span className="text-gray-700">
                    {delivery.courier === null ? "Unassigned" : delivery.courier.id}
                  </span>
                                </div>

                                <div className="text-sm">
                                    <span className="font-medium text-gray-600">Sections around: </span>
                                    <div className="ml-2 mt-1">
                                        {limitedSections.length > 0 ? (
                                            limitedSections.map((section, index) => (
                                                <div key={index} className="mb-2">
                                                    <div className="flex items-center space-x-2">
                                                        <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                                                        <span className="text-gray-700">{section.name}</span>
                                                    </div>
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
};

TextSidebar.displayName = "TextSidebar";

export default TextSidebar;
