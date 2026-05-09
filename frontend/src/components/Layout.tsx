import { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { Home, Search, PenSquare, Bell, User } from "lucide-react";
import Navbar from "./Navbar";
import { useAuthStore } from "@/store/authStore";

const tabs = [
  { to: "/feed", icon: Home, label: "Feed" },
  { to: "/search", icon: Search, label: "Search" },
  { to: "/posts/create", icon: PenSquare, label: "Create" },
  { to: "/notifications", icon: Bell, label: "Alerts" },
  { to: "/profile", icon: User, label: "Profile" },
];

export default function Layout({ children }: { children: ReactNode }) {
  const { user } = useAuthStore();
  const profileTabs = tabs.map((t) => t.to === "/profile" ? { ...t, to: `/users/${user?.id}` } : t);
  return (
    <div className="min-h-screen bg-background pb-20 md:pb-0">
      <Navbar />
      <main className="max-w-6xl mx-auto px-4 py-8">{children}</main>
      <nav className="md:hidden fixed bottom-0 inset-x-0 z-40 glass border-t border-border">
        <div className="flex justify-around py-2">
          {profileTabs.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={label}
              to={to}
              className={({ isActive }) =>
                `flex flex-col items-center gap-0.5 px-3 py-1 rounded-lg text-[10px] font-medium ${
                  isActive ? "text-brand-600" : "text-ink-muted"
                }`
              }
            >
              <Icon className="w-5 h-5" />
              {label}
            </NavLink>
          ))}
        </div>
      </nav>
    </div>
  );
}
