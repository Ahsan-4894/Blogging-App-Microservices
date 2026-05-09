import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { Eye, EyeOff, Loader2, Feather } from "lucide-react";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { useAuthStore, AuthUser } from "@/store/authStore";
import { toast } from "sonner";

export default function AuthPage({ mode }: { mode: "login" | "register" }) {
  const { user, setUser } = useAuthStore();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [shake, setShake] = useState(false);

  if (user) return <Navigate to="/feed" replace />;

  const isRegister = mode === "register";

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      if (isRegister) {
        await api.post("/auth/register", { username, email, password });
        toast.success("Welcome to Quill!");
      } else {
        await api.post("/auth/login", { username, password });
        toast.success("Welcome back!");
      }
      const userData = await api.get<AuthUser>("/users/me");
      setUser(userData);
      navigate("/feed");
    } catch (err: any) {
      setShake(true);
      setTimeout(() => setShake(false), 400);
      toast.error(err.message || "Authentication failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex">
      {/* Left panel */}
      <div className="hidden lg:flex relative w-1/2 bg-brand-gradient overflow-hidden items-center justify-center p-12">
        <div className="absolute inset-0 opacity-50">
          <div className="absolute top-10 left-10 w-72 h-72 bg-white/30 rounded-full blur-3xl animate-blob" />
          <div className="absolute bottom-10 right-10 w-96 h-96 bg-pink-300/40 rounded-full blur-3xl animate-blob" style={{ animationDelay: "4s" }} />
          <div className="absolute top-1/2 left-1/3 w-80 h-80 bg-indigo-300/40 rounded-full blur-3xl animate-blob" style={{ animationDelay: "8s" }} />
        </div>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="relative z-10 text-white max-w-md"
        >
          <div className="flex items-center gap-3 mb-8">
            <span className="w-12 h-12 rounded-2xl bg-white/20 backdrop-blur-md grid place-items-center">
              <Feather className="w-6 h-6" />
            </span>
            <span className="font-display text-3xl font-bold">Quill</span>
          </div>
          <h1 className="font-display text-5xl font-bold leading-tight mb-6">
            Where ideas find their voice.
          </h1>
          <p className="text-white/85 text-lg leading-relaxed">
            Join a community of thoughtful writers and curious readers. Publish what matters, discover what moves you.
          </p>
        </motion.div>
      </div>

      {/* Form */}
      <div className="flex-1 flex items-center justify-center p-6 lg:p-12">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          className={`w-full max-w-md ${shake ? "animate-shake" : ""}`}
        >
          <div className="lg:hidden flex items-center gap-2 mb-8">
            <span className="w-10 h-10 rounded-xl bg-brand-gradient grid place-items-center text-white">
              <Feather className="w-5 h-5" />
            </span>
            <span className="font-display text-2xl font-bold">Quill</span>
          </div>
          <h2 className="font-display text-3xl font-bold mb-2">
            {isRegister ? "Create your account" : "Welcome back"}
          </h2>
          <p className="text-ink-secondary mb-8">
            {isRegister ? "Start writing and reading today." : "Sign in to continue your story."}
          </p>

          <form onSubmit={handleSubmit} className="space-y-5">
            <FloatingInput label="Username" value={username} onChange={setUsername} required />
            {isRegister && (
              <FloatingInput label="Email" type="email" value={email} onChange={setEmail} required />
            )}
            <div className="relative">
              <FloatingInput
                label="Password"
                type={showPw ? "text" : "password"}
                value={password}
                onChange={setPassword}
                required
                minLength={8}
              />
              <button
                type="button"
                onClick={() => setShowPw((v) => !v)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-ink-muted hover:text-foreground"
              >
                {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>

            <Button
              type="submit"
              disabled={loading}
              className="w-full rounded-xl h-12 bg-brand-600 hover:bg-brand-700 text-white text-base font-semibold"
            >
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : isRegister ? "Create account" : "Sign in"}
            </Button>
          </form>

          <p className="text-center text-sm text-ink-secondary mt-6">
            {isRegister ? "Already have an account?" : "New to Quill?"}{" "}
            <Link to={isRegister ? "/login" : "/register"} className="text-brand-600 font-semibold hover:underline">
              {isRegister ? "Sign in" : "Create one"}
            </Link>
          </p>
        </motion.div>
      </div>
    </div>
  );
}

function FloatingInput({
  label, value, onChange, type = "text", required, minLength,
}: {
  label: string; value: string; onChange: (v: string) => void;
  type?: string; required?: boolean; minLength?: number;
}) {
  return (
    <div className="relative">
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required={required}
        minLength={minLength}
        placeholder=" "
        className="peer w-full h-12 px-4 pt-4 rounded-xl bg-surface-muted border border-border outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 transition"
      />
      <label className="absolute left-4 top-1 text-[10px] uppercase tracking-wider text-ink-muted peer-placeholder-shown:text-sm peer-placeholder-shown:top-3.5 peer-placeholder-shown:normal-case peer-focus:top-1 peer-focus:text-[10px] peer-focus:uppercase transition-all pointer-events-none">
        {label}
      </label>
    </div>
  );
}
