interface CustomInputProps {
  id: string;
  label: string;
  type?: string;
  value: string;
  placeholder?: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const CustomInput = ({
  id,
  label,
  type = "text",
  placeholder,
  value,
  onChange,
}: CustomInputProps) => (
  <div className="flex flex-col">
    <label className="text-gray-300 text-sm mb-1" htmlFor={id}>
      {label}
    </label>
    <input
      id={id}
      name={id}
      type={type}
      value={value}
      onChange={onChange}
      className="bg-transparent border-b border-gray-600 text-white py-2 px-1 focus:outline-none focus:border-[#e5c163] transition-colors"
      placeholder={placeholder}
    />
  </div>
);
