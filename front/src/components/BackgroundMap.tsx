"use client";

import { useState, useCallback, useRef } from "react";
// Удален useLocation, так как он нам больше не нужен
import {
  APIProvider,
  Map,
  Marker,
  type MapEvent,
} from "@vis.gl/react-google-maps";
// Define the shape of our scooter data from the backend
interface Scooter {
  id: number;
  serialNumber: string;
  chargeLevel: number; // Было batteryLevel
  status: string;
  latitude: number; // Было lat
  longitude: number; // Было lon
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

  // Создаем ссылку для хранения ID нашего таймера
  const debounceTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Функция для запроса на бэкенд (осталась почти без изменений)
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

  // Новая функция-обработчик с задержкой (Debounce)
  const handleMapIdle = useCallback(
    // Используем правильный тип MapEvent
    (e: MapEvent) => {
      // Если карты по какой-то причине нет в событии, прерываемся
      if (!e.map) return;

      // 1. Если таймер уже был запущен (юзер снова подвинул карту), отменяем его
      if (debounceTimeoutRef.current) {
        clearTimeout(debounceTimeoutRef.current);
      }

      // 2. Заводим новый таймер. Запрос уйдет только через 800 мс простоя.
      // Можете изменить 800 на 1000 (1 секунда) или 500 (полсекунды) по вкусу.
      debounceTimeoutRef.current = setTimeout(() => {
        fetchScootersInBounds(e.map);
      }, 800);
    },
    [fetchScootersInBounds],
  );

  return (
    <div className="h-full w-full bg-[#000000]">
      <APIProvider apiKey={apiKey}>
        <Map
          style={{ width: "100%", height: "100%" }}
          defaultCenter={{ lat: 50.0614, lng: 19.9383 }}
          defaultZoom={14}
          disableDefaultUI={true}
          styles={snazzyStyle}
          onIdle={handleMapIdle}
        >
          {scooters.map((scooter) => (
            <Marker
              key={scooter.id}
              position={{
                lat: scooter.latitude, // Используем правильное имя с бэкенда
                lng: scooter.longitude, // Используем правильное имя с бэкенда
              }}
              // При желании, вы даже можете использовать chargeLevel
              // для отображения разных иконок!
              title={`Самокат ${scooter.serialNumber} (Заряд: ${scooter.chargeLevel}%)`}
            />
          ))}
        </Map>
      </APIProvider>
    </div>
  );
}
