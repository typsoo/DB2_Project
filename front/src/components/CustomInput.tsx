interface CustomInputProps {
  id: string;
  label: string;
  type?: string;
  placeholder?: string;
}

export const CustomInput = ({
  id,
  label,
  type = "text",
  placeholder,
}: CustomInputProps) => (
  <div className="flex flex-col">
    <label className="text-gray-300 text-sm mb-1" htmlFor={id}>
      {label}
    </label>
    <input
      id={id}
      type={type}
      className="bg-transparent border-b border-gray-600 text-white py-2 px-1 focus:outline-none focus:border-[#e5c163] transition-colors"
      placeholder={placeholder}
    />
  </div>
);
