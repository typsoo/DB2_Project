import { useState, useEffect, useCallback } from "react";

interface Reservation {
  id: number;
  scooterId: number;
  status: string;
  expiresAt: string; // LocalDateTime from backend
}

interface ActiveReservationCardProps {
  // Callback to refresh the map or markers when reservation status changes
  onStatusChange?: () => void;
}

export default function ActiveReservationCard({
  onStatusChange,
}: ActiveReservationCardProps) {
  const [reservation, setReservation] = useState<Reservation | null>(null);
  const [timeLeft, setTimeLeft] = useState<string>("");
  const [isCancelling, setIsCancelling] = useState<boolean>(false);

  // Fetch the active reservation from your backend endpoint
  const fetchActiveReservation = useCallback(async () => {
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/reservations/me/active",
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (response.ok) {
        const data = await response.json();
        setReservation(data);
      } else if (response.status === 404) {
        // No active reservation found for this user
        setReservation(null);
      }
    } catch (error) {
      console.error("Failed to fetch active reservation:", error);
    }
  }, []);

  // Handle countdown calculation
  useEffect(() => {
    // Initial fetch on component mount
    fetchActiveReservation();

    // Set up a listener to poll data periodically or handle updates
    const globalRefreshInterval = setInterval(fetchActiveReservation, 30000); // Optional fallback poll every 30s

    return () => clearInterval(globalRefreshInterval);
  }, [fetchActiveReservation]);

  // Separate effect to drive the high-frequency 1-second countdown timer
  useEffect(() => {
    if (!reservation || !reservation.expiresAt) {
      setTimeLeft("");
      return;
    }

    const calculateTimeLeft = () => {
      const expirationTime = new Date(reservation.expiresAt).getTime();
      const currentTime = new Date().getTime();
      const difference = expirationTime - currentTime;

      if (difference <= 0) {
        setTimeLeft("Expired");
        setReservation(null); // Clear expired reservation from UI
        if (onStatusChange) onStatusChange();
        return;
      }

      // Format minutes and seconds cleanly
      const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((difference % (1000 * 60)) / 1000);

      const paddedMinutes = String(minutes).padStart(2, "0");
      const paddedSeconds = String(seconds).padStart(2, "0");

      setTimeLeft(`${paddedMinutes}:${paddedSeconds}`);
    };

    // Run calculation immediately and set up tick interval
    calculateTimeLeft();
    const countdownInterval = setInterval(calculateTimeLeft, 1000);

    return () => clearInterval(countdownInterval);
  }, [reservation, onStatusChange]);

  // Cancel reservation action
  const handleCancel = async () => {
    if (!reservation) return;

    setIsCancelling(true);
    const token = localStorage.getItem("token");

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/reservations/${reservation.id}/cancel`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (response.ok || response.status === 24) {
        // 204 No Content
        setReservation(null);
        if (onStatusChange) onStatusChange();
      }
    } catch (error) {
      console.error("Failed to cancel reservation:", error);
    } finally {
      setIsCancelling(false);
    }
  };

  // Do not render anything if there is no active reservation
  if (!reservation) return null;

  return (
    <div className="absolute top-5 left-5 z-[1000] flex flex-col p-5 min-w-[260px] rounded-2xl bg-black/40 backdrop-blur-md border border-white/20 text-white shadow-[0_8px_32px_rgba(0,0,0,0.5)] transition-all duration-300">
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">
          Active Hold
        </span>
        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase bg-[#e5c163]/20 text-[#e5c163] border border-[#e5c163]/30">
          {reservation.status}
        </span>
      </div>

      <div className="flex flex-col mb-4">
        <span className="text-gray-400 text-xs">Scooter ID</span>
        <span className="text-lg font-bold text-white">
          #{reservation.scooterId}
        </span>
      </div>

      <div className="flex flex-col items-center justify-center py-3 px-4 mb-4 rounded-xl bg-white/5 border border-white/10">
        <span className="text-[11px] text-gray-400 uppercase tracking-wide mb-0.5">
          Time Remaining
        </span>
        <span className="text-3xl font-mono font-bold text-[#e5c163] drop-shadow-[0_0_10px_rgba(229,193,99,0.3)]">
          {timeLeft}
        </span>
      </div>

      <button
        onClick={handleCancel}
        disabled={isCancelling}
        className="w-full py-2.5 bg-red-500/20 hover:bg-red-500/40 text-red-200 border border-red-500/40 font-semibold rounded-lg transition-all text-sm disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer active:scale-[0.98]"
      >
        {isCancelling ? "Cancelling..." : "Cancel Hold"}
      </button>
    </div>
  );
}
