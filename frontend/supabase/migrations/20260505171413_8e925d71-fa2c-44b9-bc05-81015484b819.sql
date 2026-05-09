
-- Profiles
create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  name text not null default '',
  bio text not null default '',
  avatar_url text,
  cover_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
alter table public.profiles enable row level security;
create policy "profiles readable by authenticated" on public.profiles for select to authenticated using (true);
create policy "users update own profile" on public.profiles for update to authenticated using (auth.uid() = id);
create policy "users insert own profile" on public.profiles for insert to authenticated with check (auth.uid() = id);

-- Posts
create table public.posts (
  id uuid primary key default gen_random_uuid(),
  author_id uuid not null references public.profiles(id) on delete cascade,
  title text not null,
  content text not null,
  tags text[] not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
alter table public.posts enable row level security;
create index posts_author_idx on public.posts(author_id);
create index posts_created_idx on public.posts(created_at desc);
create policy "posts readable by authenticated" on public.posts for select to authenticated using (true);
create policy "users insert own posts" on public.posts for insert to authenticated with check (auth.uid() = author_id);
create policy "users update own posts" on public.posts for update to authenticated using (auth.uid() = author_id);
create policy "users delete own posts" on public.posts for delete to authenticated using (auth.uid() = author_id);

-- Likes
create table public.post_likes (
  post_id uuid not null references public.posts(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (post_id, user_id)
);
alter table public.post_likes enable row level security;
create policy "likes readable by authenticated" on public.post_likes for select to authenticated using (true);
create policy "users like as self" on public.post_likes for insert to authenticated with check (auth.uid() = user_id);
create policy "users unlike own" on public.post_likes for delete to authenticated using (auth.uid() = user_id);

-- Follows
create table public.follows (
  follower_id uuid not null references public.profiles(id) on delete cascade,
  followee_id uuid not null references public.profiles(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (follower_id, followee_id),
  check (follower_id <> followee_id)
);
alter table public.follows enable row level security;
create policy "follows readable by authenticated" on public.follows for select to authenticated using (true);
create policy "users follow as self" on public.follows for insert to authenticated with check (auth.uid() = follower_id);
create policy "users unfollow own" on public.follows for delete to authenticated using (auth.uid() = follower_id);

-- Notifications
create table public.notifications (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  actor_id uuid references public.profiles(id) on delete cascade,
  type text not null,
  post_id uuid references public.posts(id) on delete cascade,
  read boolean not null default false,
  created_at timestamptz not null default now()
);
alter table public.notifications enable row level security;
create index notifications_user_idx on public.notifications(user_id, created_at desc);
create policy "users read own notifications" on public.notifications for select to authenticated using (auth.uid() = user_id);
create policy "users update own notifications" on public.notifications for update to authenticated using (auth.uid() = user_id);
create policy "system inserts notifications" on public.notifications for insert to authenticated with check (true);

-- Auto-create profile on signup
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles (id, name)
  values (new.id, coalesce(new.raw_user_meta_data->>'name', split_part(new.email, '@', 1)));
  return new;
end;
$$;

create trigger on_auth_user_created
after insert on auth.users
for each row execute function public.handle_new_user();

-- Notification triggers
create or replace function public.notify_on_like()
returns trigger language plpgsql security definer set search_path = public as $$
declare author uuid;
begin
  select author_id into author from public.posts where id = new.post_id;
  if author is not null and author <> new.user_id then
    insert into public.notifications(user_id, actor_id, type, post_id)
    values (author, new.user_id, 'like', new.post_id);
  end if;
  return new;
end; $$;
create trigger trg_notify_like after insert on public.post_likes
for each row execute function public.notify_on_like();

create or replace function public.notify_on_follow()
returns trigger language plpgsql security definer set search_path = public as $$
begin
  insert into public.notifications(user_id, actor_id, type)
  values (new.followee_id, new.follower_id, 'follow');
  return new;
end; $$;
create trigger trg_notify_follow after insert on public.follows
for each row execute function public.notify_on_follow();

-- updated_at trigger
create or replace function public.touch_updated_at()
returns trigger language plpgsql as $$
begin new.updated_at = now(); return new; end; $$;
create trigger trg_profiles_touch before update on public.profiles for each row execute function public.touch_updated_at();
create trigger trg_posts_touch before update on public.posts for each row execute function public.touch_updated_at();
