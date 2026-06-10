import BackgroundMap from "../components/BackgroundMap";
import ActiveReservationCard from "../components/ActiveReservationCard";

export default function MapPage() {
  return (
    <div className="relative h-screen w-screen overflow-hidden">
      <BackgroundMap />

      <ActiveReservationCard />
    </div>
  );
}
