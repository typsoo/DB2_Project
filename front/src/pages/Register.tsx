import AuthLayout from "../layouts/AuthLayout";
import { CustomInput } from "../components/CustomInput";

export default function Register() {
  const formFields = [
    { id: "username", label: "User's name", placeholder: "Enter your Name" },
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
      <form className="flex flex-col gap-5">
        {formFields.map((field) => (
          <CustomInput
            key={field.id}
            id={field.id}
            label={field.label}
            type={field.type}
            placeholder={field.placeholder}
          />
        ))}

        <button
          type="submit"
          className="mt-4 py-3 rounded-lg bg-[#e5c163] text-black font-bold text-lg hover:bg-[#f5d173] transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#e5c163] focus:ring-offset-black cursor-pointer"
        >
          Register
        </button>
      </form>
    </AuthLayout>
  );
}
