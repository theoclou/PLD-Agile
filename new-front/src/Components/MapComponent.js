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
import ValidateButton from './ValidateButton';  // Make sure the path is correct
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
  const [computedTour, setComputedTour] = useState(false);
  const [helpPopupVisible, setHelpPopupVisible] = useState(false);
  const [helpPopupMessage, setHelpPopupMessage] = useState("");
  const handleMouseEnterDelivery = (deliveryId) => {
    setHighlightedDeliveryId(deliveryId);
  };
  const handleMouseLeaveDelivery = () => {
    setHighlightedDeliveryId(null);
  };
  const handleHelpClick = () => {
    setHelpPopupMessage("Here are some tips to use the application.\n" +
        "Click on the 'Load Map' button to load a map.\n" +
        "Click on the 'Load Deliveries' button to load a delivery request.\n" +
        "Use the '+' and '-' buttons to add or remove delivery drivers.\n" +
        "To add a delivery point, zoom in on the map and click on the desired intersection, then click 'Add to delivery points'.\n" +
        "To remove a delivery point, click the cross in the popup window.\n" +
        "Click on the 'Compute Tour' button to calculate the delivery routes.\n"
    );
    setHelpPopupVisible(true);
  };

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
                if (result.deliveryRequests) {
                  setDeliveryData((prev) => ({
                    ...prev,
                    deliveries: result.deliveryRequests,
                  }));
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

              if (response.ok) {
                const result = await response.json();
                if (result.deliveryRequests) {
                  setDeliveryData((prev) => ({
                    ...prev,
                    deliveries: result.deliveryRequests,
                  }));
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
      setComputedTour(false); // Ajouté ic

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
          throw new Error("Failed to upload file, try again. Make sure to upload a map file.");
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

        if (!response.ok) throw new Error("Failed to upload file, try again. Make sure to load a deliveries request file.");

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

  const handleDelete = async (deliveryId) => {
    if (!deliveryId) {
      console.error("No delivery ID provided for deletion");
      return;
    }

    console.log("Attempting to delete delivery with ID:", deliveryId);
    try {
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

  const handleAddDeliveryPoint = async (intersectionId, courierID = -1) => { //TODO : add courierID to the request
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

          // Transformer les tours en routes avec information du courier
          const routesWithCourierInfo = data.tours.map((tour) => ({
            path: tour.route,
            courierId: tour.courier.id,
          }));
          setRoutesWithCouriers(routesWithCourierInfo);

          // Mise à jour des temps de retour
          setReturnTimes(() => {
            const newReturnTimes = [];
            data.tours.forEach((tour) => {
              newReturnTimes.push(tour.endTime);
            });
            return newReturnTimes;
          });

          // Mise à jour des données de livraison avec les informations des tournées
          setDeliveryData((prevData) => {
            const updatedDeliveries = [...prevData.deliveries];
            
            // Parcours des tournées
            data.tours.forEach((tour) => {
              tour.deliveryRequests.forEach((tourDelivery) => {
                const deliveryIndex = updatedDeliveries.findIndex(
                  (delivery) => delivery.deliveryAdress.id === tourDelivery.deliveryAdress.id
                );
                
                const arrivalTimeKey = `Intersection{id='${tourDelivery.deliveryAdress.id}', latitude=${tourDelivery.deliveryAdress.latitude}, longitude=${tourDelivery.deliveryAdress.longitude}}`;
                
                // Mise à jour d'une livraison existante
                if (deliveryIndex !== -1) {
                  updatedDeliveries[deliveryIndex] = {
                    ...updatedDeliveries[deliveryIndex],
                    courier: tourDelivery.courier,
                    arrivalTime: tour.arrivalTimes[arrivalTimeKey],
                  };
                } 
                // Ajout d'une nouvelle livraison
                else {
                  updatedDeliveries.push({
                    ...tourDelivery,
                    arrivalTime: tour.arrivalTimes[arrivalTimeKey],
                  });
                }
              });
            });

            // Ajout des nouvelles demandes de livraison qui ne sont pas dans les tournées
            if (data.deliveryRequests) {
              data.deliveryRequests.forEach((newDelivery) => {
                const exists = updatedDeliveries.some(
                  (delivery) => delivery.deliveryAdress.id === newDelivery.deliveryAdress.id
                );
                
                if (!exists) {
                  updatedDeliveries.push(newDelivery);
                }
              });
            }

            return {
              ...prevData,
              deliveries: updatedDeliveries,
            };
          });
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

        // Transformer les tours en routes avec information du courier
        const routesWithCourierInfo = data.tours.map((tour) => ({
          path: tour.route,
          courierId: tour.courier.id,
        }));
        setRoutesWithCouriers(routesWithCourierInfo);

        // Mise à jour des temps de retour
        setReturnTimes(() => {
          const newReturnTimes = [];
          data.tours.forEach((tour) => {
            newReturnTimes.push(tour.endTime);
          });
          return newReturnTimes;
        });

        // Mise à jour des données de livraison avec les informations des tournées
        setDeliveryData((prevData) => {
          const updatedDeliveries = [...prevData.deliveries];

          data.tours.forEach((tour) => {
            tour.deliveryRequests.forEach((tourDelivery) => {
              const deliveryIndex = updatedDeliveries.findIndex(
                (delivery) =>
                  delivery.deliveryAdress.id === tourDelivery.deliveryAdress.id
              );

              if (deliveryIndex !== -1) {
                updatedDeliveries[deliveryIndex] = {
                  ...updatedDeliveries[deliveryIndex],
                  courier: tourDelivery.courier,
                  arrivalTime:
                    tour.arrivalTimes[
                      `Intersection{id='${tourDelivery.deliveryAdress.id}', latitude=${tourDelivery.deliveryAdress.latitude}, longitude=${tourDelivery.deliveryAdress.longitude}}`
                    ],
                };
              }
            });
          });

          setComputedTour(true); // Ajout de cette ligne

          return {
            ...prevData,
            deliveries: updatedDeliveries,
          };
        });
        setTourComputed(true);
      } else {
        throw new Error("Failed to compute tour");
      }
    } catch (error) {
      console.error("Error during tour computation:", error);
      setPopupText("Error");
      setPopupMessage("Error computing tour: " + error.message);
      setPopupVisible(true);
      setComputedTour(false); // Ajout de cette ligne
    }
  };

  const handleValidateTour = async () => {
    try {
      const response = await fetch("http://localhost:8080/validateTours", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const result = await response.json(); // D'abord récupérer le résultat

      if (!response.ok) {
        throw new Error(result.message || "Failed to validate tours");
      }

      if (result.status === "success") {
        console.log("Tours validated successfully:", result.toursByCourier);
        setPopupText("Alert Success");
        setPopupMessage("Tours have been validated successfully!");
        setPopupVisible(true);
      } else {
        throw new Error(result.message || "Unknown error occurred");
      }
    } catch (error) {
      console.error("Error validating tours:", error);
      setPopupText("Error");
      setPopupMessage(error.message || "Error validating tours");
      setPopupVisible(true);
    }
  };

  return (
    <div className="container">
      <header className="header">
        <img src={boxImage} className="logo-image" />
        <h1 className="title">Pick'One</h1>
        <div className="button-container">
          <FileUploadButton onFileChange={handleFileChange} />
          {mapLoaded && <LoadDeliveryButton onFileChange={handleLoadDelivery} />}
          <CourierCounter count={courierCount} setCount={setCourierCount} />
          {deliveryLoaded && <ComputeTour onClick={handleComputeTour} />}
          <HelperButton onHelpClick={handleHelpClick} />
          {computedTour && <ValidateButton onClick={handleValidateTour} />}
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
              />
            </div>
          )}
        </div>
      )}

      {popupVisible && (
        <ErrorPopup message={popupMessage} onClose={handleClosePopup} text={popupText}/>
      )}

      {helpPopupVisible && (
          <ErrorPopup message={helpPopupMessage} onClose={() => setHelpPopupVisible(false)} />
      )}
    </div>
  );
};

export default MapComponent;
