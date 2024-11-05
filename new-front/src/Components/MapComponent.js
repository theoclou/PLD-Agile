import React, { useState, useCallback, useRef, useEffect } from "react";
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
  const [deliveryData, setDeliveryData] = useState({ deliveries: [], warehouse: null });
  const [loading, setLoading] = useState(false);
  const [bounds, setBounds] = useState(null);
  const [mapLoaded, setMapLoaded] = useState(false);
  const [zoom, setZoom] = useState(8);
  const mapRef = useRef();
  const [popupVisible, setPopupVisible] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  const [courierCount, setCourierCount] = useState(2);
  const [deliveryLoaded, setDeliveryLoaded] = useState(false);
  const [addingDeliveryPoint, setAddingDeliveryPoint] = useState(false);

  // Add keyboard event listener for undo/redo
  useEffect(() => {
    const handleKeyDown = async (event) => {
      // Check if Ctrl key (or Cmd key on Mac) is pressed
      if (event.ctrlKey || event.metaKey) {
        switch (event.key.toLowerCase()) {
          case 'z':
            event.preventDefault();
            try {
              const response = await fetch('http://localhost:8080/undo', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                },
              });

              if (response.ok) {
                const result = await response.json();
                if (result.deliveryRequests) {
                  setDeliveryData(prev => ({
                    ...prev,
                    deliveries: result.deliveryRequests
                  }));
                }
              }
            } catch (error) {
              console.error('Error during undo:', error);
            }
            break;

          case 'y':
            event.preventDefault();
            try {
              const response = await fetch('http://localhost:8080/redo', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                },
              });

              if (response.ok) {
                const result = await response.json();
                if (result.deliveryRequests) {
                  setDeliveryData(prev => ({
                    ...prev,
                    deliveries: result.deliveryRequests
                  }));
                }
              }
            } catch (error) {
              console.error('Error during redo:', error);
            }
            break;

          default:
            break;
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

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
        setDeliveryData({ deliveries: [], warehouse: null });
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
      setPopupVisible(true);
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
        const result = await response.json();
        console.log("Successfully deleted delivery:", deliveryId, "Response:", result);
        setDeliveryData((prevData) => ({
          ...prevData,
          deliveries: prevData.deliveries.filter(
            (delivery) => delivery.deliveryAdress.id !== deliveryId
          ),
        }));
      } else {
        const errorResult = await response.json();
        console.error("Failed to delete delivery request:", errorResult.message);
      }
    } catch (error) {
      console.error("Error deleting delivery request:", error);
    }
  };

  const handleIntersectionClick = async (intersectionId) => {
    console.log("ID of the Intersection clicked:", intersectionId);
    if (addingDeliveryPoint) {
      console.log("Intersection to add to delivery points:", intersectionId);

      try {
        const response = await fetch(`http://localhost:8080/addDeliveryPointById`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ intersectionId }),
        });

        if (response.ok) {
          const result = await response.json();
          console.log("Successfully adding delivery:", intersectionId, "Response:", result);

          setDeliveryData((prevData) => ({
            ...prevData,
            deliveries: [...prevData.deliveries, result.deliveryRequest],
          }));
        } else {
          const errorResult = await response.json();
          console.error("Failed to add delivery point:", errorResult.message);
        }
      } catch (error) {
        console.error("Error adding delivery request:", error);
      }

      setAddingDeliveryPoint(false);
      console.log("End of the adding mode");
    }
  };

  const handleAddDeliveryPoint = () => {
    console.log("Add Delivery Point button clicked");
    setAddingDeliveryPoint(true);
  };

  return (
    <div className="container">
      <h1 className="title">Pick'One</h1>
      <FileUploadButton onFileChange={handleFileChange} />

      {mapLoaded && <LoadDeliveryButton onFileChange={handleLoadDelivery} />}
      <CourierCounter count={courierCount} setCount={setCourierCount} />

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
            onIntersectionClick={handleIntersectionClick}
            addingDeliveryPoint={addingDeliveryPoint}
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