import { ReactNode } from "react";

export default function EmptyState({
  illustration,
  title,
  subtitle,
  cta,
}: {
  illustration?: ReactNode;
  title: string;
  subtitle?: string;
  cta?: ReactNode;
}) {
  return (
    <div className="flex flex-col items-center text-center py-16 px-6">
      <div className="w-40 h-40 mb-6 text-brand-500/60">
        {illustration ?? <DefaultIllustration />}
      </div>
      <h3 className="font-display text-2xl font-bold mb-2">{title}</h3>
      {subtitle && <p className="text-ink-secondary max-w-sm mb-6">{subtitle}</p>}
      {cta}
    </div>
  );
}

function DefaultIllustration() {
  return (
    <svg viewBox="0 0 200 200" fill="none" className="w-full h-full">
      <circle cx="100" cy="100" r="80" fill="hsl(var(--brand-100))" />
      <rect x="60" y="70" width="80" height="70" rx="8" fill="hsl(var(--card))" stroke="hsl(var(--brand-500))" strokeWidth="2" />
      <line x1="72" y1="90" x2="128" y2="90" stroke="hsl(var(--brand-500))" strokeWidth="2" strokeLinecap="round" />
      <line x1="72" y1="105" x2="120" y2="105" stroke="hsl(var(--brand-500))" strokeWidth="2" strokeLinecap="round" opacity="0.6" />
      <line x1="72" y1="120" x2="110" y2="120" stroke="hsl(var(--brand-500))" strokeWidth="2" strokeLinecap="round" opacity="0.4" />
    </svg>
  );
}
