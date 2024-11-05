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
      if (!deliveryLoaded) return;

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
  }, [deliveryLoaded]);

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
      // Reset states
      setDeliveryData({ deliveries: [], warehouse: null });
      setDeliveryLoaded(false);

      const formData = new FormData();
      formData.append("file", selectedFile);

      try {
        const response = await fetch("http://localhost:8080/loadMap", {
          method: "POST",
          body: formData,
        });
        if (!response.ok) {
          setMapLoaded(false);
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
      try {
        // First, reset the command history
        await fetch('http://localhost:8080/resetCommands', {
          method: 'POST',
        });

        const formData = new FormData();
        formData.append("file", selectedFile);

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
        setDeliveryLoaded(false);
      }
    }
  };

  const handleDelete = async (deliveryId) => {
    if (!deliveryId) {
      console.error("No delivery ID provided for deletion");
      return;
    }

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
        console.log("Delete response:", result);

        if (result.message === "Delivery request deleted successfully.") {
          setDeliveryData((prevData) => ({
            ...prevData,
            deliveries: prevData.deliveries.filter(
              (delivery) => delivery.deliveryAdress.id !== deliveryId
            ),
          }));
        } else {
          console.error("Unexpected server response:", result);
        }
      } else {
        console.error("Server returned error status:", response.status);
        const errorText = await response.text();
        console.error("Error response:", errorText);
      }
    } catch (error) {
      console.error("Error during delete request:", error);
    }
  };

  const handleIntersectionClick = async (intersectionId) => {
    if (addingDeliveryPoint) {
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
          if (result.deliveryRequest) {
            setDeliveryData((prevData) => ({
              ...prevData,
              deliveries: [...prevData.deliveries, result.deliveryRequest],
            }));
          }
        }
      } catch (error) {
        console.error("Error adding delivery request:", error);
      }
      setAddingDeliveryPoint(false);
    }
  };

  const handleAddDeliveryPoint = () => {
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
                warehouse={deliveryData.warehouse}
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