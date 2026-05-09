import { useParams, useNavigate, Link } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, Heart } from "lucide-react";
import { api } from "@/lib/api";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { tagColor } from "@/lib/tagColor";
import { format } from "date-fns";
import { toast } from "sonner";
import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { PostCardData } from "@/components/PostCard";

export default function PostDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const qc = useQueryClient();

  const { data, isLoading } = useQuery<PostCardData>({
    queryKey: ["post", id],
    queryFn: () => api.get<PostCardData>(`/posts/${id}`),
  });

  const [liked, setLiked] = useState(false);
  const [count, setCount] = useState(0);
  const [pop, setPop] = useState(false);

  useEffect(() => {
    if (data) {
      setCount(data.likeCount);
      setLiked(!!data.likedByMe);
    }
  }, [data]);

  const like = useMutation({
    mutationFn: async () => {
      if (liked) {
        return api.delete(`/posts/like/${id}`);
      } else {
        return api.post(`/posts/like/${id}`);
      }
    },
    onError: (err: any) => {
      setLiked((l) => !l);
      setCount((c) => liked ? c + 1 : c - 1);
      if (err?.status === 409) {
        setLiked(true);
        toast.success("You've already liked this ✓");
      } else {
        toast.error("Couldn't update like");
      }
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ["post", id] }),
  });

  if (isLoading || !data) {
    return (
      <div className="max-w-2xl mx-auto space-y-6">
        <div className="skeleton h-6 w-24" />
        <div className="flex gap-4 items-center"><div className="skeleton h-16 w-16 rounded-full" /><div className="space-y-2 flex-1"><div className="skeleton h-4 w-40" /><div className="skeleton h-3 w-24" /></div></div>
        <div className="skeleton h-12 w-3/4" />
        <div className="space-y-2"><div className="skeleton h-3 w-full" /><div className="skeleton h-3 w-full" /><div className="skeleton h-3 w-2/3" /></div>
      </div>
    );
  }

  return (
    <article className="max-w-2xl mx-auto">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1.5 text-sm text-ink-secondary hover:text-foreground mb-8">
        <ArrowLeft className="w-4 h-4" /> Back
      </button>

      <div className="flex items-center gap-4 mb-8">
        <Link to={`/users/${data.userId}`}>
          <Avatar className="h-16 w-16 ring-2 ring-background">
            <AvatarFallback className="bg-brand-gradient text-white text-lg font-semibold">
              {data.username?.[0]?.toUpperCase() || "?"}
            </AvatarFallback>
          </Avatar>
        </Link>
        <div className="flex-1">
          <Link to={`/users/${data.userId}`} className="font-semibold hover:text-brand-600">{data.username}</Link>
          <p className="text-sm text-ink-muted">{format(new Date(data.createdAt), "MMM d, yyyy")}</p>
        </div>
      </div>

      <h1 className="font-display text-4xl md:text-5xl font-bold leading-tight mb-4">{data.title}</h1>

      {data.tags && data.tags.length > 0 && (
        <div className="flex flex-wrap gap-1.5 mb-8">
          {data.tags.map((t: string) => <span key={t} className={`px-2.5 py-1 rounded-full text-xs font-medium ${tagColor(t)}`}>#{t}</span>)}
        </div>
      )}

      <div className="prose dark:prose-invert max-w-none prose-lg prose-headings:font-display" dangerouslySetInnerHTML={{ __html: linkify(data.content) }} />

      <div className="flex flex-col items-center mt-16 py-8 border-t border-border">
        <motion.button
          onClick={() => {
            setLiked(l => !l);
            setCount(c => liked ? c - 1 : c + 1);
            setPop(true);
            setTimeout(() => setPop(false), 300);
            like.mutate();
          }}
          whileTap={{ scale: 0.9 }}
          className={`w-20 h-20 rounded-full grid place-items-center mb-3 transition-colors ${liked ? "bg-red-50 dark:bg-red-500/10 text-red-500" : "bg-surface-muted text-ink-secondary hover:text-red-500"}`}
        >
          <Heart className={`w-9 h-9 ${liked ? "fill-current" : ""} ${pop ? "animate-pop" : ""}`} />
        </motion.button>
        <p className="text-2xl font-display font-bold">{count}</p>
        <p className="text-sm text-ink-muted">{count === 1 ? "appreciation" : "appreciations"}</p>
      </div>
    </article>
  );
}

function linkify(html: string) {
  if (html.includes("<")) return html;
  return html.split("\n\n").map(p => `<p>${p.replace(/\n/g, "<br/>")}</p>`).join("");
}
