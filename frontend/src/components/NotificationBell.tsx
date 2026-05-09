import { Bell } from "lucide-react";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";

export default function NotificationBell() {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <button className="relative p-2 rounded-full hover:bg-surface-muted" aria-label="Notifications">
          <Bell className="w-5 h-5 text-ink-secondary" />
        </button>
      </PopoverTrigger>
      <PopoverContent align="end" className="w-80 p-0">
        <div className="flex items-center justify-between p-3 border-b border-border">
          <span className="font-semibold text-sm">Notifications</span>
        </div>
        <div className="p-8 text-center text-sm text-ink-muted">
          Notifications coming soon
        </div>
      </PopoverContent>
    </Popover>
  );
}
