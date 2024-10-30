import React, { useState, useCallback, useRef } from "react";
import MapDisplay from "./MapDisplay";
import FileUploadButton from "./FileUploadButton";
import ErrorPopup from "./ErrorPopup";
import LoadDeliveryButton from "./LoadDeliveryButton";
import CourierCounter from "./CourierCounter";
import "leaflet/dist/leaflet.css";
import "./MapComponent.css";
import TextSidebar from "./TextSidebar";
import AddDeliveryPoint from "./AddDeliveryPoint";

const MapComponent = () => {
  const [mapData, setMapData] = useState({ intersections: [], sections: [] });
  const [deliveryData, setDeliveryData] = useState({ deliveries: [] });
  const [loading, setLoading] = useState(false);
  const [bounds, setBounds] = useState(null);
  const [mapLoaded, setMapLoaded] = useState(false);
  const [zoom, setZoom] = useState(8);
  const mapRef = useRef(); // Reference for the MapContainer conponent
  const [popupVisible, setPopupVisible] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  const [courierCount, setCourierCount] = useState(2); // State for the number of couriers
  const [deliveryLoaded, setDeliveryLoaded] = useState(false);
  const [addingDeliveryPoint, setAddingDeliveryPoint] = useState(false);

  //TODO check why the plan loading sometimes fails
  const handleFetchData = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/map");
      if (!response.ok) {
        setMapLoaded(false);
        throw new Error("Error during data retrieval");
      }
      const result = await response.json();
      if (result && result.intersections) {
        setMapData(result);
        setDeliveryData({ deliveries: [] });
        const latitudes = result.intersections.map((i) => i.latitude);
        const longitudes = result.intersections.map((i) => i.longitude);
        const newBounds = [
          [Math.min(...latitudes), Math.min(...longitudes)],
          [Math.max(...latitudes), Math.max(...longitudes)],
        ];
        setBounds(newBounds);
        setMapLoaded(true);

        if (mapRef.current) {
          mapRef.current.fitBounds(newBounds);
        }
      } else {
        throw new Error("Invalid data format from server");
      }
    } catch (error) {
      setPopupMessage(error.message);
      setPopupVisible(true); // Affiche le pop-up
    } finally {
      setLoading(false);
    }
  }, []);

  const handleFileChange = async (event) => {
    setLoading(true);
    const selectedFile = event.target.files[0];

    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);

      try {
        const response = await fetch("http://localhost:8080/loadMap", {
          method: "POST",
          body: formData,
        });
        if (!response.ok) {
          setMapLoaded(false);
          setDeliveryLoaded(false);
          setLoading(false);
          throw new Error("Failed to upload file, try again");
        }
        await handleFetchData();
      } catch (error) {
        setPopupMessage(error.message);
        setPopupVisible(true);
      }
    }
  };

  const handleClosePopup = () => {
    setPopupVisible(false);
  };

  const handleLoadDelivery = async (event) => {
    const selectedFile = event.target.files[0];

    if (selectedFile) {
      console.log(`File Name: ${selectedFile.name}`);
      const formData = new FormData();
      formData.append("file", selectedFile);

      try {
        const response = await fetch("http://localhost:8080/loadDelivery", {
          method: "POST",
          body: formData,
        });
        if (!response.ok) throw new Error("Failed to upload file, try again");
        const result = await response.json();
        if (result && result.deliveries) {
          setDeliveryData(result);
          setDeliveryLoaded(true);
        } else {
          throw new Error("Invalid data format from server");
        }
      } catch (error) {
        setPopupMessage(error.message);
        setPopupVisible(true);
      }
    }
  };

  const handleDelete = async (deliveryId) => {
    console.log("Attempting to delete delivery with ID:", deliveryId);
    try {
      const response = await fetch(`http://localhost:8080/deleteDeliveryRequest`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        body: deliveryId,
      });

      if (response.ok) {
        const result = await response.json(); // Assurez-vous de récupérer le message
        console.log("Successfully deleted delivery:", deliveryId, "Response:", result); // Log de confirmation
        setDeliveryData((prevData) => ({
          deliveries: prevData.deliveries.filter(
              (delivery) => delivery.deliveryAdress.id !== deliveryId
          ),
        }));
      } else {
        const errorResult = await response.json(); // Obtenez le message d'erreur
        console.error("Failed to delete delivery request:", errorResult.message); // Log d'erreur avec message
      }
    } catch (error) {
      console.error("Error deleting delivery request:", error);
    }
  };


  const handleIntersectionClick = async (intersectionId) => {
    console.log("ID of the Intersection clicked :", intersectionId);
    if (addingDeliveryPoint) {
      console.log("Intersection to add to delivery points :", intersectionId);

      try {
        const response = await fetch(`http://localhost:8080/addDeliveryPointById`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({intersectionId}),
        });

        if (response.ok) {
          const result = await response.json();
          console.log("Successfully adding delivery:", intersectionId, "Response:", result);
          console.log("Result : ", result);

          // Update of deliveryData with the new delivery point
          setDeliveryData((prevData) => ({
            deliveries: [...prevData.deliveries, result], // Add the new point
          }));

        } else {
          const errorResult = await response.json(); // Get error message
          console.error("Failed to add delivery point:", errorResult.message);
        }
      } catch (error) {
        console.error("Error adding delivery request:", error);
      }

      // Turn off the delivery point adding mode
      setAddingDeliveryPoint(false);
      console.log("End of the adding mode");
    }
  };

  const handleAddDeliveryPoint = () => {
    console.log("Add Delivery Point button clicked");
    setAddingDeliveryPoint(true); // change the state of AddingDeliveryPoint to launch the Add Delivery Point mode
  };

  return (
    <div className="container">
      <h1 className="title">Pick'One</h1>
      <FileUploadButton onFileChange={handleFileChange} />

      {mapLoaded && <LoadDeliveryButton onFileChange={handleLoadDelivery} />}
      <CourierCounter count={courierCount} setCount={setCourierCount} />

      {/* Button to add a delivery Point */}
      {deliveryLoaded && <AddDeliveryPoint onClick={handleAddDeliveryPoint} />}

      {loading && <div>Loading...</div>}

      {mapLoaded && (
        <div className="map-sidebar-container">
          <MapDisplay
            mapData={mapData}
            deliveryData={deliveryData}
            bounds={bounds}
            zoom={zoom}
            setZoom={setZoom}
            onIntersectionClick={handleIntersectionClick} // Pass the click function
            addingDeliveryPoint={addingDeliveryPoint} // Pass the state of the selection mode
          />

          {deliveryLoaded && (
            <div className="text-sidebar">
              <TextSidebar
                deliveryData={deliveryData.deliveries}
                sections={mapData.sections}
                onDelete={handleDelete}
              />
            </div>
          )}
        </div>
      )}

      {popupVisible && (
        <ErrorPopup message={popupMessage} onClose={handleClosePopup} />
      )}
    </div>
  );
};

export default MapComponent;
