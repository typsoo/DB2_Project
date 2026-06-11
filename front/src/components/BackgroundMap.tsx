"use client";

import { useState, useCallback, useRef } from "react";
import {
  APIProvider,
  Map,
  Marker,
  type MapEvent,
  InfoWindow,
} from "@vis.gl/react-google-maps";
interface Scooter {
  id: number;
  serialNumber: string;
  chargeLevel: number;
  status: string;
  latitude: number;
  longitude: number;
}

const snazzyStyle = [
  {
    featureType: "all",
    elementType: "labels",
    stylers: [
      {
        visibility: "on",
      },
    ],
  },
  {
    featureType: "all",
    elementType: "labels.text.fill",
    stylers: [
      {
        saturation: 36,
      },
      {
        color: "#000000",
      },
      {
        lightness: 40,
      },
    ],
  },
  {
    featureType: "all",
    elementType: "labels.text.stroke",
    stylers: [
      {
        visibility: "on",
      },
      {
        color: "#000000",
      },
      {
        lightness: 16,
      },
    ],
  },
  {
    featureType: "all",
    elementType: "labels.icon",
    stylers: [
      {
        visibility: "off",
      },
    ],
  },
  {
    featureType: "administrative",
    elementType: "geometry.fill",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 20,
      },
    ],
  },
  {
    featureType: "administrative",
    elementType: "geometry.stroke",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 17,
      },
      {
        weight: 1.2,
      },
    ],
  },
  {
    featureType: "administrative.country",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#e5c163",
      },
    ],
  },
  {
    featureType: "administrative.locality",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#c4c4c4",
      },
    ],
  },
  {
    featureType: "administrative.neighborhood",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#e5c163",
      },
    ],
  },
  {
    featureType: "landscape",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 20,
      },
    ],
  },
  {
    featureType: "poi",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 21,
      },
      {
        visibility: "on",
      },
    ],
  },
  {
    featureType: "poi.business",
    elementType: "geometry",
    stylers: [
      {
        visibility: "on",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "geometry.fill",
    stylers: [
      {
        color: "#e5c163",
      },
      {
        lightness: "0",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "geometry.stroke",
    stylers: [
      {
        visibility: "off",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#ffffff",
      },
    ],
  },
  {
    featureType: "road.highway",
    elementType: "labels.text.stroke",
    stylers: [
      {
        color: "#e5c163",
      },
    ],
  },
  {
    featureType: "road.arterial",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 18,
      },
    ],
  },
  {
    featureType: "road.arterial",
    elementType: "geometry.fill",
    stylers: [
      {
        color: "#575757",
      },
    ],
  },
  {
    featureType: "road.arterial",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#ffffff",
      },
    ],
  },
  {
    featureType: "road.arterial",
    elementType: "labels.text.stroke",
    stylers: [
      {
        color: "#2c2c2c",
      },
    ],
  },
  {
    featureType: "road.local",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 16,
      },
    ],
  },
  {
    featureType: "road.local",
    elementType: "labels.text.fill",
    stylers: [
      {
        color: "#999999",
      },
    ],
  },
  {
    featureType: "transit",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 19,
      },
    ],
  },
  {
    featureType: "water",
    elementType: "geometry",
    stylers: [
      {
        color: "#000000",
      },
      {
        lightness: 17,
      },
    ],
  },
];

export default function BackgroundMap() {
  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY?.trim() || "";
  const [scooters, setScooters] = useState<Scooter[]>([]);

  const [selectedScooter, setSelectedScooter] = useState<Scooter | null>(null);

  const debounceTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const fetchScootersInBounds = useCallback(
    async (mapInstance: google.maps.Map) => {
      const token = localStorage.getItem("token");
      if (!token) {
        setScooters([]);
        return;
      }

      const bounds = mapInstance.getBounds();
      if (!bounds) return;

      const minLat = bounds.getSouthWest().lat();
      const minLon = bounds.getSouthWest().lng();
      const maxLat = bounds.getNorthEast().lat();
      const maxLon = bounds.getNorthEast().lng();

      try {
        const url = `http://localhost:8080/api/scooters/area?minLat=${minLat}&minLon=${minLon}&maxLat=${maxLat}&maxLon=${maxLon}`;

        const response = await fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setScooters(data);
        }
      } catch (error) {
        console.error("Failed to fetch scooters in area:", error);
      }
    },
    [],
  );

  const handleMapIdle = useCallback(
    (e: MapEvent) => {
      if (!e.map) return;
      if (debounceTimeoutRef.current) {
        clearTimeout(debounceTimeoutRef.current);
      }
      debounceTimeoutRef.current = setTimeout(() => {
        fetchScootersInBounds(e.map);
      }, 800);
    },
    [fetchScootersInBounds],
  );

  const handleReservation = async (scooterId: number) => {
    const token = localStorage.getItem("token");
    const response = await fetch("http://localhost:8080/api/reservations", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ scooterId }),
    });

    if (response.ok) {
      setScooters((prev) => prev.filter((s) => s.id !== scooterId));
      window.dispatchEvent(new Event("reservationUpdated"));
    }

    // Close the InfoWindow after reservation
    setSelectedScooter(null);
  };

  return (
    <div className="h-full w-full bg-[#000000]">
      <APIProvider apiKey={apiKey}>
        <Map
          style={{ width: "100%", height: "100%" }}
          defaultCenter={{ lat: 50.0614, lng: 19.9383 }}
          defaultZoom={14}
          disableDefaultUI={true}
          onIdle={handleMapIdle}
          styles={snazzyStyle}
        >
          {scooters.map((scooter) => (
            <Marker
              key={scooter.id}
              position={{
                lat: scooter.latitude,
                lng: scooter.longitude,
              }}
              title={`Scooter ${scooter.serialNumber} (Battery: ${scooter.chargeLevel}%)`}
              onClick={() => setSelectedScooter(scooter)}
            />
          ))}

          {selectedScooter && (
            <InfoWindow
              position={{
                lat: selectedScooter.latitude,
                lng: selectedScooter.longitude,
              }}
              onCloseClick={() => setSelectedScooter(null)}
              pixelOffset={[0, -35]}
            >
              {/* Glassmorphism container: semi-transparent background, blur, and subtle border */}
              <div className="flex flex-col p-5 min-w-[200px] rounded-2xl bg-black/10 backdrop-blur-md border border-white/20 text-white shadow-[0_8px_32px_rgba(0,0,0,0.5)]">
                <h3 className="font-bold text-xl mb-1 text-white">
                  Scooter {selectedScooter.serialNumber}
                </h3>

                <div className="flex items-center gap-2 mb-5 text-sm text-gray-200">
                  <span className="font-semibold">Battery:</span>
                  <span
                    className={
                      selectedScooter.chargeLevel < 20
                        ? "text-red-400 font-bold drop-shadow-md"
                        : "text-[#e5c163] font-bold drop-shadow-md"
                    }
                  >
                    {selectedScooter.chargeLevel}%
                  </span>
                </div>

                <button
                  onClick={() => handleReservation(selectedScooter.id)}
                  className="w-full py-2.5 bg-[#e5c163] hover:bg-[#c29b27] text-black font-bold rounded-lg transition-all shadow-lg hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0"
                >
                  Reserve Now
                </button>
              </div>
            </InfoWindow>
          )}
        </Map>
      </APIProvider>
    </div>
  );
}
