import { useParams, Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { motion } from "framer-motion";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useCountUp } from "@/hooks/useCountUp";
import PostCard, { PostCardData } from "@/components/PostCard";
import PostSkeleton from "@/components/PostSkeleton";
import { toast } from "sonner";

const TABS = ["posts", "followers", "following"] as const;
type Tab = typeof TABS[number];

interface UserProfile {
  id: string;
  username: string;
  bio?: string;
  followerCount: number;
  followingCount: number;
  isFollowing?: boolean | null;
}

interface UserListItem {
  id: string;
  username: string;
  bio?: string;
}

export default function Profile() {
  const { userId } = useParams<{ userId: string }>();
  const { user, setUser } = useAuthStore();
  const qc = useQueryClient();
  const [tab, setTab] = useState<Tab>("posts");
  const isMe = user?.id === userId;

  const { data: profile } = useQuery<UserProfile>({
    queryKey: ["profile", userId],
    enabled: !!userId,
    queryFn: () => api.get<UserProfile>(`/users/${userId}`),
  });

  const follow = useMutation({
    mutationFn: async () => {
      if (profile?.isFollowing) {
        return api.delete(`/users/${userId}/follow`);
      } else {
        return api.post(`/users/${userId}/follow`);
      }
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["profile", userId] });
    },
    onError: (err: any) => {
      toast.error(err.message || "Action failed");
    },
  });

  if (!profile) return <ProfileSkeleton />;
  const initial = (profile.username?.[0] || "?").toUpperCase();

  return (
    <div className="max-w-3xl mx-auto">
      <div className="relative h-48 rounded-2xl bg-brand-gradient overflow-hidden">
        <div className="absolute inset-0 opacity-50">
          <div className="absolute top-0 left-10 w-48 h-48 bg-white/30 rounded-full blur-3xl animate-blob" />
          <div className="absolute bottom-0 right-10 w-56 h-56 bg-pink-300/40 rounded-full blur-3xl animate-blob" style={{ animationDelay: "3s" }} />
        </div>
      </div>

      <div className="px-4 -mt-12 flex items-end gap-4">
        <Avatar className="h-24 w-24 ring-4 ring-background shadow-md">
          <AvatarFallback className="bg-brand-gradient text-white text-3xl font-bold">{initial}</AvatarFallback>
        </Avatar>
        <div className="flex-1 pb-3">
          <h1 className="font-display text-2xl font-bold">{profile.username}</h1>
          <p className="text-ink-secondary text-sm mt-0.5 max-w-md">{profile.bio || "No bio yet."}</p>
        </div>
        {isMe ? (
          <EditProfileDialog
            profile={profile}
            onSaved={async () => {
              qc.invalidateQueries({ queryKey: ["profile", userId] });
              const updated = await api.get<any>("/users/me");
              setUser(updated);
            }}
          />
        ) : (
          <Button
            onClick={() => follow.mutate()}
            disabled={follow.isPending}
            className={`rounded-full ${profile.isFollowing ? "bg-surface-muted text-foreground border border-border hover:bg-surface" : "bg-brand-600 hover:bg-brand-700 text-white"}`}
          >
            {profile.isFollowing ? "Following" : "Follow"}
          </Button>
        )}
      </div>

      <div className="flex gap-8 px-4 mt-6">
        <Stat label="Followers" value={profile.followerCount} />
        <Stat label="Following" value={profile.followingCount} />
      </div>

      <div className="flex gap-1 border-b border-border mt-8 px-4">
        {TABS.map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`relative px-4 py-3 text-sm font-semibold capitalize ${tab === t ? "text-foreground" : "text-ink-muted hover:text-foreground"}`}
          >
            {t}
            {tab === t && <motion.div layoutId="profile-tab" className="absolute inset-x-0 -bottom-px h-0.5 bg-brand-600" />}
          </button>
        ))}
      </div>

      <div className="py-6">
        {tab === "posts" && <UserPosts userId={userId!} />}
        {tab === "followers" && <UserList userId={userId!} kind="followers" />}
        {tab === "following" && <UserList userId={userId!} kind="following" />}
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: number }) {
  const v = useCountUp(value);
  return (
    <div>
      <div className="font-display text-2xl font-bold">{v}</div>
      <div className="text-xs uppercase tracking-wider text-ink-muted">{label}</div>
    </div>
  );
}

function UserPosts({ userId }: { userId: string }) {
  const { data, isLoading } = useQuery<PostCardData[]>({
    queryKey: ["user-posts", userId],
    queryFn: () => api.get<PostCardData[]>(`/posts/by-user/${userId}`),
  });
  if (isLoading) return <div className="space-y-4"><PostSkeleton /><PostSkeleton /></div>;
  if (!data?.length) return <p className="text-center text-ink-muted py-12">No posts yet.</p>;
  return <div className="space-y-4">{data.map((p) => <PostCard key={p.id} post={p} />)}</div>;
}

function UserList({ userId, kind }: { userId: string; kind: "followers" | "following" }) {
  const { data } = useQuery<UserListItem[]>({
    queryKey: [kind, userId],
    queryFn: () => api.get<UserListItem[]>(`/users/${userId}/${kind}`),
  });
  if (!data?.length) return <p className="text-center text-ink-muted py-12">Nobody yet.</p>;
  return (
    <div className="space-y-2">
      {data.map((u) => (
        <Link key={u.id} to={`/users/${u.id}`} className="flex items-center gap-3 p-3 rounded-xl hover:bg-surface-muted">
          <Avatar className="h-10 w-10">
            <AvatarFallback className="bg-brand-gradient text-white text-sm">{u.username?.[0]?.toUpperCase()}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <p className="font-semibold text-sm">{u.username}</p>
            <p className="text-xs text-ink-muted truncate">{u.bio || "—"}</p>
          </div>
        </Link>
      ))}
    </div>
  );
}

function EditProfileDialog({ profile, onSaved }: { profile: UserProfile; onSaved: () => void }) {
  const [username, setUsername] = useState(profile.username || "");
  const [bio, setBio] = useState(profile.bio || "");
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  async function save() {
    setLoading(true);
    try {
      await api.put("/users/me", { username, bio });
      toast.success("Profile updated");
      setOpen(false);
      onSaved();
    } catch (e: any) {
      toast.error(e.message || "Failed to update profile");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline" className="rounded-full">Edit Profile</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader><DialogTitle>Edit profile</DialogTitle></DialogHeader>
        <div className="space-y-4">
          <div>
            <label className="text-sm font-medium">Username</label>
            <Input value={username} onChange={(e) => setUsername(e.target.value)} className="mt-1" />
          </div>
          <div>
            <label className="text-sm font-medium">Bio</label>
            <Textarea value={bio} onChange={(e) => setBio(e.target.value)} rows={4} className="mt-1" />
          </div>
          <Button onClick={save} disabled={loading} className="w-full bg-brand-600 hover:bg-brand-700 text-white">Save</Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

function ProfileSkeleton() {
  return (
    <div className="max-w-3xl mx-auto">
      <div className="skeleton h-48 rounded-2xl" />
      <div className="px-4 -mt-12 flex items-end gap-4">
        <div className="skeleton h-24 w-24 rounded-full" />
        <div className="space-y-2 flex-1 pb-3"><div className="skeleton h-5 w-40" /><div className="skeleton h-3 w-64" /></div>
      </div>
    </div>
  );
}
