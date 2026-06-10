import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/Register";
import MapLayout from "./layouts/MapLayout";
import Login from "./pages/Login";
import ProtectedRoute from "./layouts/ProtectedRoutes";

function App() {
  return (
    <Routes>
      <Route element={<MapLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/map" element={<div>Интерфейс вашей карты здесь</div>} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
export default App;
