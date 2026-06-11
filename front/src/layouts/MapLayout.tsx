import { Outlet } from "react-router-dom";
import BackgroundMap from "../components/BackgroundMap";

export default function MapLayout() {
  return (
    <div className="relative w-screen h-screen overflow-hidden">
      <div className="absolute inset-0 z-0">
        <BackgroundMap />
      </div>

      <div className="absolute inset-0 z-10 pointer-events-none">
        <Outlet />
      </div>
    </div>
  );
}
