import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/Register";
import MapLayout from "./layouts/MapLayout";
import Login from "./pages/Login";

function App() {
  return (
    <Routes>
      <Route element={<MapLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Route>

      <Route path="/" element={<Navigate to="/register" replace />} />
    </Routes>
  );
}
export default App;
