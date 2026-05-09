import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Placeholder from "@tiptap/extension-placeholder";
import Link from "@tiptap/extension-link";
import { Bold, Italic, Heading2, Quote, List, Link as LinkIcon, Loader2, Sparkles } from "lucide-react";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { PostCardData } from "@/components/PostCard";

export default function CreatePost() {
  const [title, setTitle] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const editor = useEditor({
    extensions: [StarterKit, Placeholder.configure({ placeholder: "Tell your story…" }), Link.configure({ openOnClick: false })],
    editorProps: { attributes: { class: "tiptap-editor" } },
  });

  async function publish() {
    if (!editor) return;
    const content = editor.getHTML();
    if (!title.trim() || editor.isEmpty) return;
    setLoading(true);
    try {
      const post = await api.post<PostCardData>("/posts", { title: title.trim(), content });
      toast.success("Published!");
      navigate(`/posts/${post.id}`);
    } catch (e: any) {
      toast.error(e.message || "Failed to publish");
    } finally {
      setLoading(false);
    }
  }

  const canPublish = title.trim() && editor && !editor.isEmpty;

  return (
    <div className="max-w-3xl mx-auto">
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="Post title…"
        className="w-full text-3xl md:text-5xl font-display font-bold bg-transparent outline-none placeholder:text-ink-muted/60 mb-6"
      />

      <div className="tiptap-editor relative">
        {editor && (
          <div className="sticky top-20 z-10 mb-4 inline-flex items-center gap-1 bg-card border border-border rounded-xl shadow-sm p-1">
            <ToolBtn active={editor.isActive("bold")} onClick={() => editor.chain().focus().toggleBold().run()}><Bold className="w-4 h-4" /></ToolBtn>
            <ToolBtn active={editor.isActive("italic")} onClick={() => editor.chain().focus().toggleItalic().run()}><Italic className="w-4 h-4" /></ToolBtn>
            <ToolBtn active={editor.isActive("heading", { level: 2 })} onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}><Heading2 className="w-4 h-4" /></ToolBtn>
            <ToolBtn active={editor.isActive("blockquote")} onClick={() => editor.chain().focus().toggleBlockquote().run()}><Quote className="w-4 h-4" /></ToolBtn>
            <ToolBtn active={editor.isActive("bulletList")} onClick={() => editor.chain().focus().toggleBulletList().run()}><List className="w-4 h-4" /></ToolBtn>
            <ToolBtn active={editor.isActive("link")} onClick={() => {
              const url = prompt("URL");
              if (url) editor.chain().focus().setLink({ href: url }).run();
            }}><LinkIcon className="w-4 h-4" /></ToolBtn>
          </div>
        )}
        <EditorContent editor={editor} />
      </div>

      <div className="flex items-center justify-between mt-8 pt-6 border-t border-border">
        <span className="inline-flex items-center gap-1.5 text-xs text-ink-muted bg-surface-muted px-3 py-1.5 rounded-full">
          <Sparkles className="w-3.5 h-3.5" /> Tags are AI-generated when you publish
        </span>
        <Button
          onClick={publish}
          disabled={!canPublish || loading}
          className="rounded-xl bg-brand-600 hover:bg-brand-700 text-white px-6"
        >
          {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : "Publish"}
        </Button>
      </div>
    </div>
  );
}

function ToolBtn({ active, onClick, children }: { active?: boolean; onClick: () => void; children: React.ReactNode }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`w-8 h-8 grid place-items-center rounded-lg transition ${active ? "bg-brand-100 text-brand-700 dark:bg-brand-500/20 dark:text-brand-500" : "text-ink-secondary hover:bg-surface-muted"}`}
    >
      {children}
    </button>
  );
}
