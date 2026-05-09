export default function PostSkeleton() {
  return (
    <div className="bg-card rounded-2xl border border-border p-5 space-y-4">
      <div className="flex items-center gap-3">
        <div className="skeleton h-10 w-10 rounded-full" />
        <div className="space-y-1.5 flex-1">
          <div className="skeleton h-3 w-32" />
          <div className="skeleton h-2.5 w-20" />
        </div>
      </div>
      <div className="skeleton h-6 w-3/4" />
      <div className="space-y-2">
        <div className="skeleton h-3 w-full" />
        <div className="skeleton h-3 w-full" />
        <div className="skeleton h-3 w-2/3" />
      </div>
      <div className="flex gap-2">
        <div className="skeleton h-5 w-14 rounded-full" />
        <div className="skeleton h-5 w-16 rounded-full" />
      </div>
    </div>
  );
}
