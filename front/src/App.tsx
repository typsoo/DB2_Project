import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/Register";
import MapLayout from "./layouts/MapLayout";
import Login from "./pages/Login";
import ProtectedRoute from "./layouts/ProtectedRoutes";
import MapPage from "./pages/MapPage";

function App() {
  return (
    <Routes>
      <Route element={<MapLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/map" element={<MapPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
export default App;
