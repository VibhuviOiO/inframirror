-- Example SQL for seeding datacenters (optional, for reference)
INSERT INTO "Datacenter" ("name", "shortName", "privateCIDR", "publicCIDR", "regionId") VALUES
  ('US East', 'use1', '10.0.0.0/16', '52.0.0.0/16', 1),
  ('US West', 'usw1', '10.1.0.0/16', '52.1.0.0/16', 1);
