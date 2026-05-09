import { create } from "zustand";
import { persist } from "zustand/middleware";
import { api } from "@/lib/api";

export interface AuthUser {
  id: string;
  username: string;
  email: string;
  role: string;
  bio?: string;
}

interface AuthState {
  user: AuthUser | null;
  initialized: boolean;
  setUser: (user: AuthUser | null) => void;
  setInitialized: () => void;
  signOut: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      initialized: false,
      setUser: (user) => set({ user, initialized: true }),
      setInitialized: () => set({ initialized: true }),
      signOut: async () => {
        await api.post("/auth/logout").catch(() => {});
        set({ user: null });
      },
    }),
    { name: "quill-auth", partialize: (s) => ({ user: s.user }) }
  )
);

export async function initAuth() {
  try {
    const user = await api.get<AuthUser>("/users/me");
    useAuthStore.getState().setUser(user);
  } catch {
    useAuthStore.getState().setInitialized();
  }
}
