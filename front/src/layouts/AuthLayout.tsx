import type { ReactNode } from "react";
import { Link } from "react-router-dom";

interface AuthLayoutProps {
  title: string;
  children: ReactNode;
  footerText: string;
  footerLinkText: string;
  footerLinkTo: string;
}

export default function AuthLayout({
  title,
  children,
  footerText,
  footerLinkText,
  footerLinkTo,
}: AuthLayoutProps) {
  return (
    <div className="h-full w-full flex items-center justify-center bg-black/20 backdrop-blur-md pointer-events-auto px-4">
      <div className="w-full max-w-md lg:max-w-none lg:w-1/2 xl:w-1/3 p-8 rounded-2xl bg-black/40 border border-[#e5c163] shadow-2xl max-h-[95vh] overflow-y-auto custom-scrollbar">
        <h2 className="text-3xl font-bold text-center text-white mb-6">
          {title}
        </h2>

        {children}

        <p className="mt-6 text-center text-gray-400">
          {footerText}{" "}
          <Link
            to={footerLinkTo}
            className="text-[#e5c163] hover:text-[#f5d173] transition-colors underline"
          >
            {footerLinkText}
          </Link>
        </p>
      </div>
    </div>
  );
}
