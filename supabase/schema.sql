-- HomeCash Supabase / PostgreSQL baseline schema.
-- Run in Supabase SQL Editor. RLS is enabled on every user-owned table.
create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  email text,
  display_name text not null default '',
  mode text not null default 'personal' check (mode in ('personal','business')),
  business_name text not null default '',
  business_type text not null default '',
  monthly_budget numeric(14,2) not null default 0 check (monthly_budget >= 0),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.devices (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  device_name text not null,
  status text not null default 'OFFLINE' check (status in ('ONLINE','SYNCING','OFFLINE')),
  last_sync_at timestamptz,
  created_at timestamptz not null default now(),
  unique (user_id, id)
);
create table if not exists public.lines (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id text,
  phone_number text not null default '',
  carrier text not null default '',
  display_name text not null default '',
  sim_slot_index integer not null default 0,
  is_primary boolean not null default true,
  created_at timestamptz not null default now()
);
create table if not exists public.wallets (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  line_id text,
  provider_name text not null,
  current_balance numeric(14,2) not null default 0,
  initial_balance numeric(14,2) not null default 0,
  send_fee_pct numeric(7,4) not null default 0,
  send_fee_fixed numeric(14,2) not null default 0,
  receive_fee_pct numeric(7,4) not null default 0,
  receive_fee_fixed numeric(14,2) not null default 0,
  color_hex text not null default '#4F63E8',
  is_active boolean not null default true,
  last_updated timestamptz not null default now(),
  created_at timestamptz not null default now()
);
create table if not exists public.transactions (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  wallet_id text not null,
  device_id text not null,
  sms_id text,
  fingerprint text not null,
  direction text not null check (direction in ('IN','OUT')),
  amount numeric(14,2) not null check (amount >= 0),
  fee numeric(14,2) not null default 0,
  cashier_commission numeric(14,2) not null default 0,
  balance_after numeric(14,2),
  counterparty_number text not null default '',
  counterparty_name text not null default '',
  operation_code text not null default '',
  category text not null default '',
  notes text not null default '',
  raw_text text not null default '',
  occurred_at timestamptz not null,
  status text not null default 'CONFIRMED',
  is_auto_categorized boolean not null default false,
  note text not null default '',
  is_manual boolean not null default false,
  is_synced boolean not null default false,
  created_at timestamptz not null default now(),
  unique(user_id, fingerprint)
);
create table if not exists public.flags (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  wallet_id text not null,
  transaction_id text,
  type text not null,
  status text not null default 'OPEN' check (status in ('OPEN','RESOLVED','IGNORED')),
  title text not null,
  explanation text not null default '',
  expected_balance numeric(14,2),
  actual_balance numeric(14,2),
  discrepancy numeric(14,2),
  created_at timestamptz not null default now(),
  resolved_at timestamptz
);
create table if not exists public.category_rules (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  counterparty_number text not null,
  category text not null,
  counterparty_name text not null default '',
  created_at timestamptz not null default now(),
  unique(user_id, counterparty_number)
);
create table if not exists public.shift_closings (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id text not null default '',
  cashier_name text not null default 'كاشير الوردية الأولى',
  opened_at timestamptz not null default now(),
  expected_cash numeric(14,2) not null,
  actual_cash numeric(14,2) not null,
  difference numeric(14,2) not null,
  total_inward numeric(14,2) not null default 0,
  total_outward numeric(14,2) not null default 0,
  total_commission numeric(14,2) not null default 0,
  notes text not null default '',
  closed_at timestamptz not null default now()
);

-- A profile is created automatically for a newly registered Auth user.
create or replace function public.handle_new_homecash_user()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
  insert into public.profiles (id, email, display_name)
  values (new.id, new.email, coalesce(new.raw_user_meta_data ->> 'name', split_part(coalesce(new.email,''), '@', 1)))
  on conflict (id) do nothing;
  return new;
end;
$$;
drop trigger if exists on_homecash_auth_user_created on auth.users;
create trigger on_homecash_auth_user_created after insert on auth.users
for each row execute procedure public.handle_new_homecash_user();

alter table public.profiles enable row level security;
alter table public.devices enable row level security;
alter table public.lines enable row level security;
alter table public.wallets enable row level security;
alter table public.transactions enable row level security;
alter table public.flags enable row level security;
alter table public.category_rules enable row level security;
alter table public.shift_closings enable row level security;

do $$
declare t text;
begin
  foreach t in array array['profiles','devices','lines','wallets','transactions','flags','category_rules','shift_closings'] loop
    execute format('drop policy if exists "homecash_owner_all" on public.%I', t);
    if t = 'profiles' then
      execute format('create policy "homecash_owner_all" on public.%I for all to authenticated using (id = auth.uid()) with check (id = auth.uid())', t);
    else
      execute format('create policy "homecash_owner_all" on public.%I for all to authenticated using (user_id = auth.uid()) with check (user_id = auth.uid())', t);
    end if;
  end loop;
end $$;

create index if not exists transactions_user_occurred_idx on public.transactions(user_id, occurred_at desc);
create index if not exists transactions_user_counterparty_idx on public.transactions(user_id, counterparty_number);
create index if not exists flags_user_status_idx on public.flags(user_id, status);
create index if not exists wallets_user_idx on public.wallets(user_id);
