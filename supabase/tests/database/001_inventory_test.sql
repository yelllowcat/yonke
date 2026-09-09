begin;
select plan(8);

select has_table('public', 'vehicles', 'vehicles table exists');
select has_table('public', 'parts', 'parts table exists');
select has_function('public', 'sell_part', array['uuid','uuid','uuid','uuid'], 'atomic sell function exists');
select has_view('public', 'public_catalog_parts', 'safe public projection exists');
select col_is_pk('public', 'request_results', array['business_id','request_id'], 'idempotency key is scoped to business');
select results_eq(
  $$ select count(*)::bigint from public.public_catalog_parts $$,
  array[4::bigint],
  'seed exposes four available published parts'
);
select results_eq(
  $$ select count(*)::bigint from information_schema.columns where table_schema = 'public' and table_name = 'public_catalog_parts' and column_name = 'location_note' $$,
  array[0::bigint],
  'public projection omits location'
);
select results_eq(
  $$ select count(*)::bigint from information_schema.columns where table_schema = 'public' and table_name = 'public_catalog_parts' and column_name = 'vin' $$,
  array[0::bigint],
  'public projection omits VIN'
);

select * from finish();
rollback;

