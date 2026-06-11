import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../layouts/AuthLayout";
import { CustomInput } from "../components/CustomInput";
import { Navigate } from "react-router-dom";

export default function Login() {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  if (localStorage.getItem("token")) {
    return <Navigate to="/map" replace />;
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault(); // Prevent default browser page reload
    setIsLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.token);
        navigate("/map");
      } else {
        const errorData = await response.json();
        setError(errorData.message);
        return;
      }
    } catch (err) {
      setError("Failed to connect to the server. Please try again.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const formFields = [
    { id: "email", label: "Email", placeholder: "Enter email", type: "email" },
    {
      id: "password",
      label: "Password",
      placeholder: "Enter your password",
      type: "password",
    },
  ];

  return (
    <AuthLayout
      title="Log in"
      footerText="Don't have an account?"
      footerLinkText="Register"
      footerLinkTo="/register"
    >
      <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
        {error && (
          <div className="p-3 rounded bg-red-500/20 border border-red-500 text-red-200 text-sm">
            {error}
          </div>
        )}
        {formFields.map((field) => (
          <CustomInput
            key={field.id}
            id={field.id}
            label={field.label}
            type={field.type}
            placeholder={field.placeholder}
            value={formData[field.id as keyof typeof formData]}
            onChange={handleChange}
          />
        ))}
        <div className="flex justify-end text-sm text-gray-400 hover:text-[#f5d173] transition-colors">
          <a href="#">Forgot password?</a>
        </div>
        <button
          type="submit"
          disabled={isLoading}
          className="mt-4 py-3 rounded-lg bg-[#e5c163] text-black font-bold text-lg hover:bg-[#f5d173] transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#e5c163] focus:ring-offset-black cursor-pointer"
        >
          {isLoading ? "Logging..." : "Log in"}
        </button>
      </form>
    </AuthLayout>
  );
}
