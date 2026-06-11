import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../layouts/AuthLayout";
import { CustomInput } from "../components/CustomInput";
import { Navigate } from "react-router-dom";

export default function Register() {
  const navigate = useNavigate();

  // Initialize state for all registration fields
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  if (localStorage.getItem("token")) {
    return <Navigate to="/map" replace />;
  }

  // Handle input changes dynamically
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault();
    setError("");

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match");
      return; // Stop execution if passwords are different
    }

    setIsLoading(true);

    try {
      // Send registration request to the backend
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        // We do not send confirmPassword to the backend, only required fields
        body: JSON.stringify({
          firstName: formData.name,
          lastName: formData.surname,

          email: formData.email,
          password: formData.password,
        }),
      });

      if (response.ok) {
        navigate("/login");
      } else {
        // Attempt to extract the error message from the backend (e.g., "Email already in use")
        const errorData = await response.json().catch(() => null);
        setError(
          errorData?.message || "Registration failed. Please try again.",
        );
      }
    } catch (err) {
      setError("Failed to connect to the server. Please check your network.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const formFields = [
    { id: "name", label: "User's name", placeholder: "Enter your Name" },
    {
      id: "surname",
      label: "User's surname",
      placeholder: "Enter your Surname",
    },

    { id: "email", label: "Email", placeholder: "Enter email", type: "email" },
    {
      id: "password",
      label: "Password",
      placeholder: "Enter your password",
      type: "password",
    },
    {
      id: "confirmPassword",
      label: "Password confirmation",
      placeholder: "Confirm your password",
      type: "password",
    },
  ];

  return (
    <AuthLayout
      title="Registration"
      footerText="You already have an account?"
      footerLinkText="Log in"
      footerLinkTo="/login"
    >
      <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
        {/* Display error message if it exists */}
        {error && (
          <div className="p-3 rounded bg-red-500/20 border border-red-500 text-red-200 text-sm">
            {error}
          </div>
        )}

        {formFields.map((field) => (
          <CustomInput
            key={field.id}
            id={field.id} // This also acts as the 'name' attribute in CustomInput
            label={field.label}
            type={field.type}
            placeholder={field.placeholder}
            value={formData[field.id as keyof typeof formData]}
            onChange={handleChange}
          />
        ))}

        <button
          type="submit"
          disabled={isLoading}
          className="mt-4 py-3 rounded-lg bg-[#e5c163] text-black font-bold text-lg hover:bg-[#f5d173] 
          transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#e5c163] focus:ring-offset-black cursor-pointer"
        >
          {isLoading ? "Registering..." : "Register"}
        </button>
      </form>
    </AuthLayout>
  );
}
