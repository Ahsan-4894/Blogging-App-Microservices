import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Search as SearchIcon } from "lucide-react";
import { api } from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import { useDebounce } from "@/hooks/useDebounce";
import PostCard, { PostCardData } from "@/components/PostCard";
import PostSkeleton from "@/components/PostSkeleton";
import EmptyState from "@/components/EmptyState";

interface SearchResult {
  id: string;
  userId: string;
  username: string;
  title: string;
  content: string;
  tags: string[];
  createdAt: string;
}

function toPostCard(r: SearchResult): PostCardData {
  return {
    id: r.id,
    userId: r.userId,
    username: r.username || "Unknown",
    title: r.title,
    content: r.content,
    tags: r.tags,
    likeCount: 0,
    likedByMe: false,
    createdAt: r.createdAt,
  };
}

export default function Search() {
  const [params, setParams] = useSearchParams();
  const [q, setQ] = useState(params.get("q") || "");
  const debounced = useDebounce(q, 400);
  const { user } = useAuthStore();

  useEffect(() => { setParams(debounced ? { q: debounced } : {}); }, [debounced, setParams]);

  const { data, isLoading } = useQuery<PostCardData[]>({
    queryKey: ["search", debounced, user?.id],
    enabled: !!debounced && !!user,
    queryFn: async () => {
      const results = await api.get<SearchResult[]>(`/search?q=${encodeURIComponent(debounced)}`);
      return results.map(toPostCard);
    },
  });

  return (
    <div className="max-w-2xl mx-auto">
      <div className="relative mb-8">
        <SearchIcon className="w-5 h-5 text-ink-muted absolute left-5 top-1/2 -translate-y-1/2" />
        <input
          autoFocus
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Search posts, tags, ideas…"
          className="w-full h-14 pl-14 pr-5 rounded-full bg-card border border-border outline-none focus:ring-2 focus:ring-brand-500/30 focus:border-brand-500 text-lg"
        />
      </div>

      {!debounced && (
        <EmptyState title="Start typing to search" subtitle="Find posts, ideas, and writers worth reading." />
      )}

      {debounced && isLoading && (
        <div className="space-y-4">{Array.from({ length: 3 }).map((_, i) => <PostSkeleton key={i} />)}</div>
      )}

      {debounced && !isLoading && data && data.length === 0 && (
        <EmptyState title={`No results for "${debounced}"`} subtitle="Try different keywords or fewer terms." />
      )}

      <div className="space-y-4">
        {data?.map((p) => <PostCard key={p.id} post={p} />)}
      </div>
    </div>
  );
}
