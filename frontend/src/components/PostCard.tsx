import { motion, AnimatePresence } from "framer-motion";
import { Heart, Share2 } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { formatDistanceToNow } from "date-fns";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { tagColor } from "@/lib/tagColor";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { toast } from "sonner";

export interface PostCardData {
  id: string;
  userId: string;
  username?: string | null;
  title: string;
  content: string;
  tags: string[] | null;
  likeCount: number;
  likedByMe?: boolean | null;
  createdAt: string;
  updatedAt?: string;
}

export default function PostCard({ post }: { post: PostCardData }) {
  const [expanded, setExpanded] = useState(false);
  const [liked, setLiked] = useState(!!post.likedByMe);
  const [count, setCount] = useState(post.likeCount);
  const [pop, setPop] = useState(false);
  const qc = useQueryClient();
  const navigate = useNavigate();

  const toggleLike = useMutation({
    mutationFn: async () => {
      if (liked) {
        return api.delete(`/posts/like/${post.id}`);
      } else {
        return api.post(`/posts/like/${post.id}`);
      }
    },
    onError: (err: any) => {
      setLiked(!!post.likedByMe);
      setCount(post.likeCount);
      if (err?.status === 409) {
        setLiked(true);
        toast.success("You've already liked this ✓");
      } else {
        toast.error("Couldn't update like");
      }
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["feed"] });
    },
  });

  const handleLike = (e: React.MouseEvent) => {
    e.stopPropagation();
    setLiked((l) => !l);
    setCount((c) => (liked ? c - 1 : c + 1));
    setPop(true);
    setTimeout(() => setPop(false), 300);
    toggleLike.mutate();
  };

  const initial = (post.userId?.[0] || "?").toUpperCase();

  return (
    <article
      onClick={() => navigate(`/posts/${post.id}`)}
      className="group bg-card rounded-2xl border border-border shadow-sm hover:shadow-md hover:-translate-y-0.5 transition-all duration-200 p-5 cursor-pointer"
    >
      <div className="flex items-center gap-3 mb-3">
        <Link to={`/users/${post.userId}`} onClick={(e) => e.stopPropagation()}>
          <Avatar className="h-10 w-10 ring-2 ring-background">
            <AvatarFallback className="bg-brand-gradient text-white text-sm font-semibold">{initial}</AvatarFallback>
          </Avatar>
        </Link>
        <div className="flex-1 min-w-0">
          <Link
            to={`/users/${post.userId}`}
            onClick={(e) => e.stopPropagation()}
            className="font-semibold text-sm hover:text-brand-600"
          >
            {post.username || "Anonymous"}
          </Link>
          <p className="text-xs text-ink-muted">
            {formatDistanceToNow(new Date(post.createdAt), { addSuffix: true })}
          </p>
        </div>
      </div>

      <h2 className="font-display font-bold text-2xl leading-snug mb-2 group-hover:text-brand-600 transition-colors">
        {post.title}
      </h2>

      <div className={expanded ? "" : "line-clamp-3"}>
        <p className="text-ink-secondary leading-relaxed whitespace-pre-wrap">{stripHtml(post.content)}</p>
      </div>
      {post.content.length > 200 && (
        <button
          onClick={(e) => { e.stopPropagation(); setExpanded((v) => !v); }}
          className="text-brand-600 text-sm font-medium mt-1 hover:underline"
        >
          {expanded ? "Show less" : "Read more"}
        </button>
      )}

      {post.tags && post.tags.length > 0 && (
        <div className="flex flex-wrap gap-1.5 mt-4">
          {post.tags.map((t) => (
            <span key={t} className={`px-2.5 py-1 rounded-full text-xs font-medium ${tagColor(t)}`}>#{t}</span>
          ))}
        </div>
      )}

      <div className="flex items-center justify-between mt-5 pt-4 border-t border-border">
        <button
          onClick={handleLike}
          className={`flex items-center gap-1.5 text-sm font-medium ${liked ? "text-red-500" : "text-ink-secondary hover:text-red-500"}`}
        >
          <Heart className={`w-5 h-5 ${liked ? "fill-current" : ""} ${pop ? "animate-pop" : ""}`} />
          {count}
        </button>
        <button onClick={(e) => e.stopPropagation()} className="text-ink-muted hover:text-foreground">
          <Share2 className="w-4 h-4" />
        </button>
      </div>
    </article>
  );
}

function stripHtml(s: string) {
  return s.replace(/<[^>]+>/g, "").trim();
}
