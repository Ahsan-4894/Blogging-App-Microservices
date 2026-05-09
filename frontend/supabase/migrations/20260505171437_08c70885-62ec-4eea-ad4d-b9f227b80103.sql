
-- Revoke execute on internal functions from public/authenticated
revoke execute on function public.handle_new_user() from public, anon, authenticated;
revoke execute on function public.notify_on_like() from public, anon, authenticated;
revoke execute on function public.notify_on_follow() from public, anon, authenticated;
revoke execute on function public.touch_updated_at() from public, anon, authenticated;

-- Restrict notifications insert (only triggers run as definer; block direct API insert)
drop policy if exists "system inserts notifications" on public.notifications;
create policy "no direct notification insert" on public.notifications for insert to authenticated with check (false);
