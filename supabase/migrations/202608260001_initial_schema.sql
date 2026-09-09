create extension if not exists pgcrypto with schema extensions;
create extension if not exists unaccent with schema extensions;
create extension if not exists pg_trgm with schema extensions;

create type public.staff_role as enum ('owner', 'staff');
create type public.vehicle_status as enum ('active', 'archived');
create type public.part_status as enum ('available', 'sold', 'archived');
create type public.part_condition as enum ('tested', 'good', 'untested', 'cosmetic_wear', 'damaged');

create table public.businesses (
  id uuid primary key default gen_random_uuid(),
  slug text not null unique check (slug ~ '^[a-z0-9-]+$'),
  name text not null check (length(trim(name)) between 2 and 100),
  phone_display text not null,
  phone_e164 text not null check (phone_e164 ~ '^[0-9]{10,15}$'),
  whatsapp_e164 text not null check (whatsapp_e164 ~ '^[0-9]{10,15}$'),
  city text not null,
  hours_es text not null default 'Lun–Sáb · 8:00–18:00',
  hours_en text not null default 'Mon–Sat · 8:00–18:00',
  next_vehicle_number integer not null default 1001,
  next_part_number integer not null default 1001,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table public.owner_profiles (
  user_id uuid primary key references auth.users(id) on delete cascade,
  business_id uuid not null references public.businesses(id) on delete cascade,
  display_name text not null,
  created_at timestamptz not null default now()
);

create table public.staff_members (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  display_name text not null check (length(trim(display_name)) between 2 and 60),
  pin_hash text not null,
  role public.staff_role not null default 'staff',
  is_active boolean not null default true,
  failed_pin_attempts integer not null default 0 check (failed_pin_attempts >= 0),
  locked_until timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (business_id, display_name)
);

create table public.device_enrollments (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  code_hash text not null unique,
  expires_at timestamptz not null,
  redeemed_at timestamptz,
  created_by uuid references auth.users(id),
  created_by_staff uuid references public.staff_members(id),
  created_at timestamptz not null default now()
  ,check ((created_by is not null)::int + (created_by_staff is not null)::int = 1)
);

create table public.devices (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  name text not null,
  credential_hash text not null unique,
  is_active boolean not null default true,
  last_seen_at timestamptz,
  created_at timestamptz not null default now()
);

create table public.staff_sessions (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  staff_id uuid not null references public.staff_members(id) on delete cascade,
  device_id uuid not null references public.devices(id) on delete cascade,
  token_hash text not null unique,
  expires_at timestamptz not null,
  created_at timestamptz not null default now()
);

create table public.vehicles (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  reference text,
  year integer not null check (year between 1900 and 2100),
  make text not null check (length(trim(make)) between 1 and 60),
  model text not null check (length(trim(model)) between 1 and 60),
  vin text check (vin is null or length(trim(vin)) between 6 and 24),
  color text,
  location_note text not null default '',
  status public.vehicle_status not null default 'active',
  revision integer not null default 1,
  created_by uuid references public.staff_members(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (business_id, reference)
);

create table public.parts (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  donor_vehicle_id uuid not null references public.vehicles(id),
  reference text,
  name_es text not null check (length(trim(name_es)) between 2 and 100),
  name_en text not null check (length(trim(name_en)) between 2 and 100),
  category_es text not null check (length(trim(category_es)) between 2 and 80),
  category_en text not null check (length(trim(category_en)) between 2 and 80),
  aliases text[] not null default '{}',
  condition public.part_condition not null,
  condition_note text not null default '',
  location_note text not null default '',
  price_mxn_cents integer check (price_mxn_cents is null or price_mxn_cents >= 0),
  is_published boolean not null default false,
  status public.part_status not null default 'available',
  sold_at timestamptz,
  sold_by uuid references public.staff_members(id),
  revision integer not null default 1,
  created_by uuid references public.staff_members(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique (business_id, reference)
);

create table public.photos (
  id uuid primary key default gen_random_uuid(),
  business_id uuid not null references public.businesses(id) on delete cascade,
  vehicle_id uuid references public.vehicles(id) on delete cascade,
  part_id uuid references public.parts(id) on delete cascade,
  storage_path text not null unique,
  public_url text,
  alt_es text not null default '',
  alt_en text not null default '',
  sort_order integer not null default 0,
  created_at timestamptz not null default now(),
  check ((vehicle_id is not null)::int + (part_id is not null)::int = 1)
);

create table public.audit_events (
  id bigint generated always as identity primary key,
  business_id uuid not null references public.businesses(id) on delete cascade,
  actor_staff_id uuid references public.staff_members(id),
  actor_owner_id uuid references auth.users(id),
  device_id uuid references public.devices(id),
  entity_type text not null,
  entity_id uuid not null,
  action text not null,
  before_data jsonb,
  after_data jsonb,
  created_at timestamptz not null default now(),
  check ((actor_staff_id is not null)::int + (actor_owner_id is not null)::int = 1)
);

create table public.request_results (
  business_id uuid not null references public.businesses(id) on delete cascade,
  request_id uuid not null,
  route text not null,
  status_code integer not null,
  response_body jsonb not null,
  created_at timestamptz not null default now(),
  primary key (business_id, request_id)
);

create table public.part_aliases (
  id bigint generated always as identity primary key,
  category_es text not null,
  category_en text not null,
  aliases text[] not null,
  unique (category_es, category_en)
);

create index parts_business_status_idx on public.parts (business_id, status, is_published);
create index parts_vehicle_idx on public.parts (donor_vehicle_id);
create index vehicles_business_status_idx on public.vehicles (business_id, status);
create index parts_aliases_gin_idx on public.parts using gin (aliases);
create index staff_sessions_token_idx on public.staff_sessions (token_hash, expires_at);
create index audit_business_created_idx on public.audit_events (business_id, created_at desc);

create or replace function public.assign_inventory_reference()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  assigned_number integer;
begin
  if new.reference is not null and trim(new.reference) <> '' then
    return new;
  end if;

  if tg_table_name = 'vehicles' then
    update public.businesses
      set next_vehicle_number = next_vehicle_number + 1
      where id = new.business_id
      returning next_vehicle_number - 1 into assigned_number;
    new.reference := 'V-' || assigned_number;
  else
    update public.businesses
      set next_part_number = next_part_number + 1
      where id = new.business_id
      returning next_part_number - 1 into assigned_number;
    new.reference := 'P-' || assigned_number;
  end if;
  return new;
end;
$$;

create trigger vehicles_assign_reference before insert on public.vehicles
for each row execute function public.assign_inventory_reference();
create trigger parts_assign_reference before insert on public.parts
for each row execute function public.assign_inventory_reference();

create or replace function public.bump_revision()
returns trigger language plpgsql as $$
begin
  new.updated_at := now();
  new.revision := old.revision + 1;
  return new;
end;
$$;

create trigger vehicles_bump_revision before update on public.vehicles
for each row execute function public.bump_revision();
create trigger parts_bump_revision before update on public.parts
for each row execute function public.bump_revision();

create or replace function public.verify_staff_pin(p_staff_id uuid, p_pin text)
returns table (staff_id uuid, business_id uuid, role public.staff_role, display_name text)
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  member public.staff_members%rowtype;
begin
  select * into member from public.staff_members where id = p_staff_id for update;
  if not found or not member.is_active then return; end if;
  if member.locked_until is not null and member.locked_until > now() then return; end if;

  if member.pin_hash = crypt(p_pin, member.pin_hash) then
    update public.staff_members set failed_pin_attempts = 0, locked_until = null where id = p_staff_id;
    return query select member.id, member.business_id, member.role, member.display_name;
  end if;

  update public.staff_members
  set failed_pin_attempts = failed_pin_attempts + 1,
      locked_until = case when failed_pin_attempts + 1 >= 5 then now() + interval '15 minutes' else null end
  where id = p_staff_id;
end;
$$;

create or replace function public.create_staff_member(
  p_business_id uuid,
  p_display_name text,
  p_pin text,
  p_role public.staff_role default 'staff'
)
returns public.staff_members
language plpgsql
security definer
set search_path = public, extensions
as $$
declare
  created public.staff_members%rowtype;
begin
  if p_pin !~ '^[0-9]{4}$' then raise exception 'invalid_pin'; end if;
  insert into public.staff_members (business_id, display_name, pin_hash, role)
  values (p_business_id, trim(p_display_name), crypt(p_pin, gen_salt('bf')), p_role)
  returning * into created;
  return created;
end;
$$;

create or replace function public.sell_part(
  p_part_id uuid,
  p_business_id uuid,
  p_actor_staff_id uuid,
  p_device_id uuid
)
returns public.parts
language plpgsql
security definer
set search_path = public
as $$
declare
  prior public.parts%rowtype;
  sold public.parts%rowtype;
begin
  select * into prior from public.parts
  where id = p_part_id and business_id = p_business_id
  for update;
  if not found then raise exception 'part_not_found'; end if;
  if prior.status <> 'available' then raise exception 'part_not_available'; end if;

  update public.parts set status = 'sold', sold_at = now(), sold_by = p_actor_staff_id
  where id = p_part_id returning * into sold;

  insert into public.audit_events (
    business_id, actor_staff_id, device_id, entity_type, entity_id, action, before_data, after_data
  ) values (
    p_business_id, p_actor_staff_id, p_device_id, 'part', p_part_id, 'sold', to_jsonb(prior), to_jsonb(sold)
  );
  return sold;
end;
$$;

create or replace function public.restore_part(
  p_part_id uuid,
  p_business_id uuid,
  p_actor_owner_id uuid
)
returns public.parts
language plpgsql
security definer
set search_path = public
as $$
declare
  prior public.parts%rowtype;
  restored public.parts%rowtype;
begin
  if not exists (
    select 1 from public.owner_profiles where user_id = p_actor_owner_id and business_id = p_business_id
  ) then raise exception 'owner_required'; end if;

  select * into prior from public.parts
  where id = p_part_id and business_id = p_business_id
  for update;
  if not found then raise exception 'part_not_found'; end if;
  if prior.status <> 'sold' then raise exception 'part_not_sold'; end if;

  update public.parts set status = 'available', sold_at = null, sold_by = null
  where id = p_part_id returning * into restored;

  insert into public.audit_events (
    business_id, actor_owner_id, entity_type, entity_id, action, before_data, after_data
  ) values (
    p_business_id, p_actor_owner_id, 'part', p_part_id, 'restored', to_jsonb(prior), to_jsonb(restored)
  );
  return restored;
end;
$$;

create or replace function public.restore_part_as_staff_owner(
  p_part_id uuid,
  p_business_id uuid,
  p_actor_staff_id uuid,
  p_device_id uuid
)
returns public.parts
language plpgsql
security definer
set search_path = public
as $$
declare
  prior public.parts%rowtype;
  restored public.parts%rowtype;
begin
  if not exists (
    select 1 from public.staff_members
    where id = p_actor_staff_id and business_id = p_business_id and role = 'owner' and is_active
  ) then raise exception 'owner_required'; end if;

  select * into prior from public.parts
  where id = p_part_id and business_id = p_business_id
  for update;
  if not found then raise exception 'part_not_found'; end if;
  if prior.status <> 'sold' then raise exception 'part_not_sold'; end if;

  update public.parts set status = 'available', sold_at = null, sold_by = null
  where id = p_part_id returning * into restored;

  insert into public.audit_events (
    business_id, actor_staff_id, device_id, entity_type, entity_id, action, before_data, after_data
  ) values (
    p_business_id, p_actor_staff_id, p_device_id, 'part', p_part_id, 'restored', to_jsonb(prior), to_jsonb(restored)
  );
  return restored;
end;
$$;

create view public.public_catalog_parts
with (security_invoker = true)
as
select
  p.id,
  p.business_id,
  b.slug as business_slug,
  b.name as business_name,
  b.phone_display,
  b.phone_e164,
  b.whatsapp_e164,
  b.city,
  b.hours_es,
  b.hours_en,
  p.reference,
  p.name_es,
  p.name_en,
  p.category_es,
  p.category_en,
  p.aliases,
  p.condition,
  p.price_mxn_cents,
  p.updated_at,
  v.year,
  v.make,
  v.model,
  v.color,
  photo.public_url as photo_url
from public.parts p
join public.vehicles v on v.id = p.donor_vehicle_id and v.status = 'active'
join public.businesses b on b.id = p.business_id
left join lateral (
  select ph.public_url from public.photos ph
  where ph.part_id = p.id and ph.public_url is not null
  order by ph.sort_order, ph.created_at
  limit 1
) photo on true
where p.status = 'available' and p.is_published;

alter table public.businesses enable row level security;
alter table public.owner_profiles enable row level security;
alter table public.staff_members enable row level security;
alter table public.device_enrollments enable row level security;
alter table public.devices enable row level security;
alter table public.staff_sessions enable row level security;
alter table public.vehicles enable row level security;
alter table public.parts enable row level security;
alter table public.photos enable row level security;
alter table public.audit_events enable row level security;
alter table public.request_results enable row level security;
alter table public.part_aliases enable row level security;

revoke all on all tables in schema public from anon, authenticated;
revoke all on all functions in schema public from anon, authenticated;
grant usage on schema public to anon, authenticated, service_role;
grant all on all tables in schema public to service_role;
grant all on all sequences in schema public to service_role;
grant execute on all functions in schema public to service_role;

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('inventory-photos', 'inventory-photos', true, 10485760, array['image/jpeg', 'image/png', 'image/webp'])
on conflict (id) do update set public = excluded.public, file_size_limit = excluded.file_size_limit, allowed_mime_types = excluded.allowed_mime_types;
