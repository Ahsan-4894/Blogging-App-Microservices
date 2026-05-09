import { Link, NavLink, useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { useState } from "react";
import { Home, Search, PenSquare, Bell, User, Sun, Moon, LogOut, Feather } from "lucide-react";
import { useAuthStore } from "@/store/authStore";
import { useTheme } from "@/hooks/useTheme";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import NotificationBell from "./NotificationBell";

export default function Navbar() {
  const { user, signOut } = useAuthStore();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [searchOpen, setSearchOpen] = useState(false);
  const [q, setQ] = useState("");

  const initial = (user?.username?.[0] || "U").toUpperCase();

  return (
    <header className="sticky top-0 z-40 glass">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center gap-3">
        <Link to="/feed" className="flex items-center gap-2 group shrink-0">
          <span className="w-9 h-9 rounded-xl bg-brand-gradient grid place-items-center text-white shadow-sm group-hover:scale-105 transition-transform">
            <Feather className="w-4 h-4" />
          </span>
          <span className="font-display text-xl font-bold tracking-tight hidden sm:inline">Quill</span>
        </Link>

        <div className="flex-1 flex items-center justify-center">
          <motion.div
            layout
            className="hidden md:flex items-center bg-surface-muted rounded-full border border-border overflow-hidden"
            animate={{ width: searchOpen ? 380 : 44 }}
            transition={{ type: "spring", stiffness: 260, damping: 28 }}
          >
            <button
              onClick={() => setSearchOpen((v) => !v)}
              className="w-11 h-11 grid place-items-center text-ink-secondary hover:text-foreground"
              aria-label="Search"
            >
              <Search className="w-4 h-4" />
            </button>
            {searchOpen && (
              <input
                autoFocus
                value={q}
                onChange={(e) => setQ(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && q.trim()) {
                    navigate(`/search?q=${encodeURIComponent(q.trim())}`);
                    setSearchOpen(false);
                  }
                }}
                placeholder="Search posts, tags, people…"
                className="bg-transparent outline-none text-sm flex-1 pr-4"
              />
            )}
          </motion.div>
        </div>

        <ThemeSwitch theme={theme} onToggle={toggleTheme} />

        <Button
          onClick={() => navigate("/posts/create")}
          className="hidden sm:inline-flex rounded-full bg-brand-600 hover:bg-brand-700 text-white shadow-sm"
        >
          <PenSquare className="w-4 h-4 mr-1.5" /> New Post
        </Button>

        <NotificationBell />

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <button className="rounded-full ring-2 ring-transparent hover:ring-brand-500/30 transition">
              <Avatar className="h-9 w-9">
                <AvatarFallback className="bg-brand-gradient text-white font-semibold">{initial}</AvatarFallback>
              </Avatar>
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuItem onClick={() => user && navigate(`/users/${user.id}`)}>
              <User className="w-4 h-4 mr-2" /> Profile
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={async () => { await signOut(); navigate("/login"); }}>
              <LogOut className="w-4 h-4 mr-2" /> Logout
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}

function ThemeSwitch({ theme, onToggle }: { theme: "light" | "dark"; onToggle: () => void }) {
  return (
    <button
      onClick={onToggle}
      className="relative bg-surface-muted rounded-full p-1 flex gap-1 border border-border"
      aria-label="Toggle theme"
    >
      {(["light", "dark"] as const).map((opt) => {
        const active = theme === opt;
        return (
          <span key={opt} className="relative px-3 py-1.5 rounded-full text-xs font-medium flex items-center gap-1.5 z-10">
            {active && (
              <motion.span
                layoutId="theme-bubble"
                className="absolute inset-0 rounded-full bg-card shadow-sm border border-border"
                transition={{ type: "spring", stiffness: 400, damping: 32 }}
              />
            )}
            <span className={`relative flex items-center gap-1.5 ${active ? "text-foreground" : "text-ink-muted"}`}>
              {opt === "light" ? <Sun className="w-3.5 h-3.5" /> : <Moon className="w-3.5 h-3.5" />}
              <span className="hidden sm:inline capitalize">{opt}</span>
            </span>
          </span>
        );
      })}
    </button>
  );
}
