import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Route, Routes, Navigate, useLocation } from "react-router-dom";
import { Toaster } from "sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AnimatePresence, motion } from "framer-motion";
import { useAuthStore } from "@/store/authStore";
import AuthPage from "./pages/Auth";
import Feed from "./pages/Feed";
import PostDetail from "./pages/PostDetail";
import CreatePost from "./pages/CreatePost";
import Search from "./pages/Search";
import Profile from "./pages/Profile";
import Layout from "./components/Layout";
import NotFound from "./pages/NotFound.tsx";

const queryClient = new QueryClient({
  defaultOptions: { queries: { staleTime: 30_000, refetchOnWindowFocus: false } },
});

function Protected({ children }: { children: React.ReactNode }) {
  const { user, initialized } = useAuthStore();
  if (!initialized) return <div className="min-h-screen flex items-center justify-center"><div className="skeleton h-12 w-32" /></div>;
  if (!user) return <Navigate to="/login" replace />;
  return <Layout>{children}</Layout>;
}

function AnimatedRoutes() {
  const location = useLocation();
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<AuthPage mode="login" />} />
        <Route path="/register" element={<AuthPage mode="register" />} />
        <Route path="/" element={<Navigate to="/feed" replace />} />
        <Route path="/feed" element={<Protected><Page><Feed /></Page></Protected>} />
        <Route path="/posts/create" element={<Protected><Page><CreatePost /></Page></Protected>} />
        <Route path="/posts/:id" element={<Protected><Page><PostDetail /></Page></Protected>} />
        <Route path="/search" element={<Protected><Page><Search /></Page></Protected>} />
        <Route path="/users/:userId" element={<Protected><Page><Profile /></Page></Protected>} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AnimatePresence>
  );
}

function Page({ children }: { children: React.ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -8 }}
      transition={{ duration: 0.25, ease: "easeOut" }}
    >
      {children}
    </motion.div>
  );
}

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster position="top-center" richColors closeButton />
      <BrowserRouter>
        <AnimatedRoutes />
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
