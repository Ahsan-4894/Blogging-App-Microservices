import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { initAuth } from "./store/authStore";

initAuth(); // fire-and-forget; Protected route shows skeleton until initialized
createRoot(document.getElementById("root")!).render(<App />);
