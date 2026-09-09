insert into public.businesses (
  id, slug, name, phone_display, phone_e164, whatsapp_e164, city, hours_es, hours_en, next_vehicle_number, next_part_number
) values (
  '11111111-1111-4111-8111-111111111111', 'yonke-facil', 'Yonke Fácil', '669 123 4567', '526691234567', '526691234567',
  'Mazatlán, Sinaloa', 'Lun–Sáb · 8:00–18:00', 'Mon–Sat · 8:00–18:00', 1005, 1059
) on conflict (id) do nothing;

insert into public.staff_members (id, business_id, display_name, pin_hash, role)
values
  ('22222222-2222-4222-8222-222222222221', '11111111-1111-4111-8111-111111111111', 'Ana', crypt('1234', gen_salt('bf')), 'staff'),
  ('22222222-2222-4222-8222-222222222222', '11111111-1111-4111-8111-111111111111', 'David', crypt('2468', gen_salt('bf')), 'owner')
on conflict (id) do nothing;

-- Local smoke-test phone. This seed file is for development/staging only.
insert into public.devices (id, business_id, name, credential_hash)
values (
  '55555555-5555-4555-8555-555555555555',
  '11111111-1111-4111-8111-111111111111',
  'Teléfono de prueba',
  encode(digest('local-device-credential', 'sha256'), 'hex')
)
on conflict (id) do nothing;

insert into public.vehicles (id, business_id, reference, year, make, model, color, location_note, created_by)
values
  ('33333333-3333-4333-8333-333333333331', '11111111-1111-4111-8111-111111111111', 'V-1001', 2012, 'Nissan', 'Sentra', 'Plata', 'Junto a la entrada', '22222222-2222-4222-8222-222222222221'),
  ('33333333-3333-4333-8333-333333333332', '11111111-1111-4111-8111-111111111111', 'V-1002', 2015, 'Chevrolet', 'Aveo', 'Blanco', 'Atrás del taller', '22222222-2222-4222-8222-222222222221'),
  ('33333333-3333-4333-8333-333333333333', '11111111-1111-4111-8111-111111111111', 'V-1003', 2018, 'Kia', 'Rio', 'Rojo', 'Patio del fondo', '22222222-2222-4222-8222-222222222221'),
  ('33333333-3333-4333-8333-333333333334', '11111111-1111-4111-8111-111111111111', 'V-1004', 2014, 'Ford', 'Fiesta', 'Azul', 'Frente al rack', '22222222-2222-4222-8222-222222222221')
on conflict (id) do nothing;

insert into public.parts (
  id, business_id, donor_vehicle_id, reference, name_es, name_en, category_es, category_en,
  aliases, condition, location_note, price_mxn_cents, is_published, created_by
) values
  ('4cf2e131-71ea-4c96-8e09-8d48f5a82201', '11111111-1111-4111-8111-111111111111', '33333333-3333-4333-8333-333333333331', 'P-1042', 'Alternador', 'Alternator', 'Sistema eléctrico', 'Electrical', array['alternador','alternator'], 'tested', 'Estante junto al Sentra', 145000, true, '22222222-2222-4222-8222-222222222221'),
  ('4cf2e131-71ea-4c96-8e09-8d48f5a82202', '11111111-1111-4111-8111-111111111111', '33333333-3333-4333-8333-333333333332', 'P-1047', 'Faro delantero izquierdo', 'Left headlight', 'Carrocería', 'Body', array['faro','headlight','lámpara'], 'good', 'Caja azul del taller', 90000, true, '22222222-2222-4222-8222-222222222221'),
  ('4cf2e131-71ea-4c96-8e09-8d48f5a82203', '11111111-1111-4111-8111-111111111111', '33333333-3333-4333-8333-333333333333', 'P-1051', 'Puerta trasera derecha', 'Right rear door', 'Carrocería', 'Body', array['puerta','door'], 'cosmetic_wear', 'Apoyada junto al Kia', null, true, '22222222-2222-4222-8222-222222222221'),
  ('4cf2e131-71ea-4c96-8e09-8d48f5a82204', '11111111-1111-4111-8111-111111111111', '33333333-3333-4333-8333-333333333334', 'P-1058', 'Compresor de aire', 'A/C compressor', 'Clima', 'Climate', array['compresor','aire','ac compressor'], 'untested', 'Rack junto al Ford', 120000, true, '22222222-2222-4222-8222-222222222221')
on conflict (id) do nothing;

insert into public.part_aliases (category_es, category_en, aliases) values
  ('Sistema eléctrico', 'Electrical', array['alternador','alternator','marcha','starter','arnés','wiring']),
  ('Carrocería', 'Body', array['puerta','door','cofre','hood','faro','headlight','defensa','bumper']),
  ('Clima', 'Climate', array['aire','air conditioning','compresor','compressor'])
on conflict do nothing;
