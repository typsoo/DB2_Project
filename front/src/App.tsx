import { Routes, Route } from "react-router-dom";
import ScooterMap from "./pages/Map";
import Login from "./pages/Login";

function App() {
  return (
    <>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/map" element={<ScooterMap />} />
      </Routes>
    </>
  );
}

export default App;
