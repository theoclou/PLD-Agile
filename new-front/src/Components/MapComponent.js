import React, { useState, useCallback, useRef, useEffect } from "react";
import MapDisplay from "./MapDisplay";
import FileUploadButton from "./FileUploadButton";
import ErrorPopup from "./ErrorPopup";
import LoadDeliveryButton from "./LoadDeliveryButton";
import CourierCounter from "./CourierCounter";
import "leaflet/dist/leaflet.css";
import "./MapComponent.css";
import TextSidebar from "./TextSidebar";
import ComputeTour from "./ComputeTour";
import ValidateButton from "./ValidateButton"; // Make sure the path is correct
import logoImage from "../Assets/logo.png";
import boxImage from "../Assets/box2.png";
import HelperButton from "./HelperButton";

const MapComponent = () => {
  const [mapData, setMapData] = useState({ intersections: [], sections: [] });
  const [deliveryData, setDeliveryData] = useState({
    deliveries: [],
    warehouse: null,
  });
  const [loading, setLoading] = useState(false);
  const [bounds, setBounds] = useState(null);
  const [mapLoaded, setMapLoaded] = useState(false);
  const [tourComputed, setTourComputed] = useState(false);
  const [zoom, setZoom] = useState(8);
  const mapRef = useRef();
  const [popupVisible, setPopupVisible] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  const [popupText, setPopupText] = useState("");
  const [courierCount, setCourierCount] = useState(2);
  const [deliveryLoaded, setDeliveryLoaded] = useState(false);
  const [highlightedDeliveryId, setHighlightedDeliveryId] = useState(null);
  const [routesWithCouriers, setRoutesWithCouriers] = useState([]);
  const [returnTimes, setReturnTimes] = useState([]);
  const [helpPopupVisible, setHelpPopupVisible] = useState(false);
  const [helpPopupMessage, setHelpPopupMessage] = useState("");
  const [expandedCouriers, setExpandedCouriers] = useState({});

  const handleMouseEnterDelivery = (deliveryId) => {
    setHighlightedDeliveryId(deliveryId);
  };
  const handleMouseLeaveDelivery = () => {
    setHighlightedDeliveryId(null);
  };

  const updateTour = useCallback(
    (data) => {
      if (!data.tours || data.tours.length === 0) {
        console.error("No tours data received");
        return;
      }

      // Update routes with courier information
      const routesWithCourierInfo = data.tours.map((tour) => ({
        path: tour.route,
        courierId: tour.courier.id,
      }));
      setRoutesWithCouriers(routesWithCourierInfo);

      // Update return times
      const newReturnTimes = data.tours.map((tour) => tour.endTime);
      setReturnTimes(newReturnTimes);

      // Update delivery data with tour information
      const updatedDeliveries = [];
      data.tours.forEach((tour) => {
        tour.deliveryRequests.forEach((delivery) => {
          // Skip warehouse
          if (delivery.deliveryAdress.id === deliveryData.warehouse?.id) return;

          // Format the key as it appears in the arrivalTimes map
          const arrivalTimeKey = `Intersection{id='${delivery.deliveryAdress.id}', latitude=${delivery.deliveryAdress.latitude}, longitude=${delivery.deliveryAdress.longitude}}`;

          const arrivalTimeStr = tour.arrivalTimes[arrivalTimeKey];
          let arrivalTime = null;

          if (arrivalTimeStr) {
            // Parse the time string (format "HH:mm:ss")
            const [hours, minutes, seconds] = arrivalTimeStr
              .split(":")
              .map(Number);
            arrivalTime = { hours, minutes, seconds };
          }

          updatedDeliveries.push({
            ...delivery,
            courier: tour.courier,
            arrivalTime,
          });
        });
      });

      setDeliveryData((prevData) => ({
        ...prevData,
        deliveries: updatedDeliveries,
      }));

      console.log("Updated delivery data:", updatedDeliveries);
      setTourComputed(true);
    },
    [deliveryData.warehouse]
  );

  const handleHelpClick = () => {
    setHelpPopupMessage(
      "Here are some tips to use the application.\n" +
        "Click on the 'Load Map' button to load a map.\n" +
        "Click on the 'Load Deliveries' button to load a delivery request.\n" +
        "Use the '+' and '-' buttons to add or remove delivery drivers.\n" +
        "To add a delivery point, zoom in on the map and click on the desired intersection, then click 'Add to delivery points'.\n" +
        "To remove a delivery point, click the cross in the popup window.\n" +
        "Click on the 'Compute Tour' button to calculate the delivery routes.\n"
    );
    setHelpPopupVisible(true);
  };

  useEffect(() => {
    const handleBeforeUnload = async () => {
      try {
        const response = await fetch("http://localhost:8080/resetCommands", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          keepalive: true,
        });
      } catch (error) {
        console.error("Error resetting commands:", error);
      }
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, []);

  // Add keyboard event listener for undo/redo
  useEffect(() => {
    const handleKeyDown = async (event) => {
      // Check if Ctrl key (or Cmd key on Mac) is pressed
      if (!deliveryLoaded) return;

      if (event.ctrlKey || event.metaKey) {
        switch (event.key.toLowerCase()) {
          case "z":
            event.preventDefault();
            try {
              const response = await fetch("http://localhost:8080/undo", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
              });

              if (response.ok) {
                const result = await response.json();
                console.log("Undo response:", result);
                if (result.deliveryRequests) {
                  setDeliveryData((prev) => ({
                    ...prev,
                    deliveries: result.deliveryRequests,
                  }));
                }
                if (tourComputed) {
                  updateTour(result);
                }
              }
            } catch (error) {
              console.error("Error during undo:", error);
            }
            break;

          case "y":
            event.preventDefault();
            try {
              const response = await fetch("http://localhost:8080/redo", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
              });

              console.log("response " + response.ok);
              if (response.ok) {
                const result = await response.json();
                console.log("Redo response:", result);
                if (result.deliveryRequests) {
                  setDeliveryData((prev) => ({
                    ...prev,
                    deliveries: result.deliveryRequests,
                  }));
                }
                if (tourComputed) {
                  updateTour(result);
                }
              }
            } catch (error) {
              console.error("Error during redo:", error);
            }
            break;

          default:
            break;
        }
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [deliveryLoaded, tourComputed, updateTour]);

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
      setPopupText("Error");
      setPopupMessage(error.message);
      setPopupVisible(true);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleFileChange = async (event) => {
    const selectedFile = event.target.files[0];

    if (selectedFile) {
      // Reset states
      setLoading(true);
      setDeliveryData({ deliveries: [], warehouse: null });
      setDeliveryLoaded(false);
      setRoutesWithCouriers([]);
      setTourComputed(false); // Ajouté ic

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
          throw new Error(
            "Failed to upload file, try again. Make sure to upload a map file."
          );
        }
        await handleFetchData();
      } catch (error) {
        setPopupText("Error");
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
    //Reset states
    setDeliveryData({ deliveries: [], warehouse: null });
    setDeliveryLoaded(false);
    setRoutesWithCouriers([]);
    setTourComputed(false);

    if (selectedFile) {
      try {
        // First, reset the command history
        await fetch("http://localhost:8080/resetCommands", {
          method: "POST",
        });

        const formData = new FormData();
        formData.append("file", selectedFile);

        const response = await fetch("http://localhost:8080/loadDelivery", {
          method: "POST",
          body: formData,
        });

        if (!response.ok)
          throw new Error(
            "Failed to upload file, try again. Make sure to load a deliveries request file."
          );

        const result = await response.json();
        if (result && result.deliveries) {
          setDeliveryData(result);
          setDeliveryLoaded(true);
        } else {
          throw new Error("Invalid data format from server");
        }
      } catch (error) {
        setPopupText("Error");
        setPopupMessage(error.message);
        setPopupVisible(true);
        setDeliveryLoaded(false);
      }
    }
  };

  const handleDelete = async (deliveryId, courierId = -1) => {
    if (!deliveryId) {
      console.error("No delivery ID provided for deletion");
      return;
    }

    console.log("Attempting to delete delivery with ID:", deliveryId);
    try {
      console.log("tour computed " + tourComputed);
      if (!tourComputed) {
        const response = await fetch(
          `http://localhost:8080/deleteDeliveryRequest`,
          {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
            },
            body: deliveryId,
          }
        );
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
          }
        }
      } else {
        const response = await fetch(
          `http://localhost:8080/deleteDeliveryRequestWithCourier`,
          {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ deliveryId, courierId }),
          }
        );
        console.log("response " + response);

        if (response.ok) {
          const data = await response.json();
          console.log("Delete response:", data);
          if (data.status === "success") {
            // Use the existing updateTour function to handle all state updates
            updateTour(data);
          } else {
            throw new Error(data.message || "Failed to delete delivery point");
          }
        } else {
          const data = await response.json();
          throw new Error(data.message);
        }
      }
    } catch (error) {
      console.error("Error during delete request:", error);
      setPopupText("Error");
      setPopupMessage("Error deleting delivery point: " + error.message);
      setPopupVisible(true);
    }
  };

  // Handle setting the warehouse
  const handleSetWarehouse = async (intersectionId) => {
    setDeliveryData({ deliveries: [], warehouse: null });
    setDeliveryLoaded(false);
    setRoutesWithCouriers([]);
    try {
      const response = await fetch(
        `http://localhost:8080/defineWarehouseById`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ intersectionId }),
        }
      );

      if (response.ok) {
        const result = await response.json();
        if (result.Warehouse) {
          setDeliveryData((prev) => ({
            ...prev,
            warehouse: result.Warehouse,
          }));
          setDeliveryData({ deliveries: [], warehouse: result.Warehouse });
          setDeliveryLoaded(true);
        }
      }
    } catch (error) {
      setPopupMessage(error.message);
      setPopupVisible(true);
      setDeliveryLoaded(false);
      console.error("Error while defining the warehouse:", error);
    }
  };

  const handleAddDeliveryPoint = async (intersectionId, courierID = -1) => {
    //TODO : add courierID to the request
    console.log("delivery loaded " + deliveryLoaded);
    if (!deliveryLoaded) return; //TODO check ça
    try {
      console.log("Adding delivery point with ID:", intersectionId);
      if (!tourComputed) {
        const response = await fetch(
          `http://localhost:8080/addDeliveryPointById`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ intersectionId }),
          }
        );

        if (response.ok) {
          const result = await response.json();
          if (result.deliveryRequest) {
            setDeliveryData((prevData) => ({
              ...prevData,
              deliveries: [...prevData.deliveries, result.deliveryRequest],
            }));
          }
          setPopupVisible(false);
        } else {
          throw new Error("Failed to add delivery point");
        }
      } else {
        console.log("Courier : " + courierID);
        const response = await fetch(
          `http://localhost:8080/addDeliveryPointByIdAfterCompute`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ intersectionId, courierID }),
          }
        );

        if (response.ok) {
          const data = await response.json();
          console.log("Add delivery point response:", data);
          updateTour(data);
          setTourComputed(true);
        } else {
          throw new Error("Failed to add delivery point " + response.message);
        }
      }
    } catch (error) {
      console.error("Error while adding a delivery point:", error);
    }
  };

  const setCourierNumber = async (courierNumber) => {
    try {
      const response = await fetch("http://localhost:8080/couriers", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ count: courierNumber }),
      });
      if (!response.ok) {
        throw new Error("Failed to set courier number");
      }
    } catch (error) {
      throw new Error("Failed to set courier number");
    }
  };

  const handleComputeTour = async () => {
    try {
      // Set the number of couriers
      await setCourierNumber(courierCount);
      const response = await fetch("http://localhost:8080/compute", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const data = await response.json();

        updateTour(data);
        setTourComputed(true);
      } else {
        throw new Error("Failed to compute tour");
      }
    } catch (error) {
      console.error("Error during tour computation:", error);
      setPopupText("Error");
      setPopupMessage("Error computing tour: " + error.message);
      setPopupVisible(true);
      setTourComputed(false); // Ajout de cette ligne
    }
  };

  const handleValidateTour = async () => {
    try {
      const response = await fetch("http://localhost:8080/downloadReport", {
        method: "GET",
      });

      if (!response.ok) {
        throw new Error("Failed to download report");
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `delivery_tours_${new Date().toISOString().slice(0, 19).replace(/:/g, "")}.txt`;

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      setPopupText("Success");
      setPopupMessage("Tours have been validated and downloaded successfully!");
      setPopupVisible(true);
    } catch (error) {
      console.error("Error downloading report:", error);
      setPopupText("Error");
      setPopupMessage(error.message || "Error downloading report");
      setPopupVisible(true);
    }
  };

  useEffect(() => {
    console.log("Delivery data:", deliveryData);
    console.log("Return Times" + returnTimes);
  }, [deliveryData, returnTimes]);
  return (
    <div className="container">
      <header className="header">
        <img src={boxImage} className="logo-image" />
        <h1 className="title">Pick'One</h1>
        <div className="button-container">
          <FileUploadButton onFileChange={handleFileChange} />
          {mapLoaded && (
            <LoadDeliveryButton onFileChange={handleLoadDelivery} />
          )}
          <CourierCounter count={courierCount} setCount={setCourierCount} />
          {deliveryLoaded && <ComputeTour onClick={handleComputeTour} />}
          <HelperButton onHelpClick={handleHelpClick} />
          {tourComputed && <ValidateButton onClick={handleValidateTour} />}
        </div>
      </header>

      {!mapLoaded && (
        <div className="welcome-container">
          <img src={logoImage} alt="Welcome" className="welcome-image" />
        </div>
      )}

      {loading && <div>Loading...</div>}

      {mapLoaded && (
        <div className="map-sidebar-container">
          <div className="map-container">
            <MapDisplay
              mapData={mapData}
              deliveryData={deliveryData}
              bounds={bounds}
              zoom={zoom}
              setZoom={setZoom}
              addingDeliveryPoint={handleAddDeliveryPoint}
              highlightedDeliveryId={highlightedDeliveryId}
              onMouseEnterDelivery={handleMouseEnterDelivery}
              onMouseLeaveDelivery={handleMouseLeaveDelivery}
              routes={routesWithCouriers}
              returnTimes={returnTimes}
              tourComputed={tourComputed}
              numberOfCouriers={courierCount}
              setWarehouse={handleSetWarehouse} // Pass handleSetWarehouse method
              hasDeliveries={deliveryLoaded}
            />
          </div>

          {deliveryLoaded && (
            <div className="text-sidebar">
              <TextSidebar
                deliveryData={deliveryData.deliveries}
                sections={mapData.sections}
                onDelete={handleDelete}
                warehouse={deliveryData.warehouse}
                highlightedDeliveryId={highlightedDeliveryId}
                onMouseEnterDelivery={handleMouseEnterDelivery}
                onMouseLeaveDelivery={handleMouseLeaveDelivery}
                expandedCouriers={expandedCouriers}
                setExpandedCouriers={setExpandedCouriers}
              />
            </div>
          )}
        </div>
      )}

      {popupVisible && (
        <ErrorPopup
          message={popupMessage}
          onClose={handleClosePopup}
          text={popupText}
        />
      )}

      {helpPopupVisible && (
        <ErrorPopup
          message={helpPopupMessage}
          onClose={() => setHelpPopupVisible(false)}
        />
      )}
    </div>
  );
};

export default MapComponent;
