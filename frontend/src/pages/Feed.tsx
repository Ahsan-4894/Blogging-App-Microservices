import { useQuery } from "@tanstack/react-query";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import PostCard, { PostCardData } from "@/components/PostCard";
import PostSkeleton from "@/components/PostSkeleton";
import EmptyState from "@/components/EmptyState";
import { Button } from "@/components/ui/button";

export default function Feed() {
  const { user } = useAuthStore();

  const { data: posts = [], isLoading } = useQuery<PostCardData[]>({
    queryKey: ["feed", user?.id],
    enabled: !!user,
    queryFn: () => api.get<PostCardData[]>("/feed"),
  });

  return (
    <div className="max-w-2xl mx-auto">
      <div className="sticky top-16 z-20 -mx-4 px-4 py-3 bg-background/80 backdrop-blur-md border-b border-border mb-6">
        <div className="flex gap-1">
          {["For You", "Following"].map((t, i) => (
            <button key={t} className={`relative px-4 py-2 text-sm font-semibold ${i === 0 ? "text-foreground" : "text-ink-muted"}`}>
              {t}
              {i === 0 && <motion.div layoutId="feed-tab" className="absolute -bottom-3 left-2 right-2 h-0.5 bg-brand-600 rounded-full" />}
            </button>
          ))}
        </div>
      </div>

      {isLoading && <div className="space-y-4">{Array.from({ length: 3 }).map((_, i) => <PostSkeleton key={i} />)}</div>}

      {!isLoading && posts.length === 0 && (
        <EmptyState
          title="Your feed is quiet"
          subtitle="Be the first to publish something, or explore posts from the community."
          cta={
            <div className="flex gap-3">
              <Button asChild className="rounded-xl bg-brand-600 hover:bg-brand-700 text-white"><Link to="/posts/create">Write a post</Link></Button>
              <Button asChild variant="outline" className="rounded-xl"><Link to="/search">Explore</Link></Button>
            </div>
          }
        />
      )}

      <div className="space-y-4">
        {posts.map((p) => <PostCard key={p.id} post={p} />)}
      </div>
    </div>
  );
}
