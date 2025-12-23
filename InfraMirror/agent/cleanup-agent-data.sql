-- Cleanup script for agent data
-- Based on cache: agentId=1152, regionId=3, datacenterId=1101, instanceId=1202

-- Delete instance heartbeats
DELETE FROM instance_heartbeats WHERE instance_id = 1202;

-- Delete instance
DELETE FROM instances WHERE id = 1202;

-- Delete agent
DELETE FROM agents WHERE id = 1152;

-- Delete datacenter (if no other agents use it)
DELETE FROM datacenters WHERE id = 1101 AND NOT EXISTS (
    SELECT 1 FROM agents WHERE datacenter_id = 1101
);

-- Delete region (if no other datacenters use it)
DELETE FROM regions WHERE id = 3 AND NOT EXISTS (
    SELECT 1 FROM datacenters WHERE region_id = 3
);

-- Verify cleanup
SELECT 'Agents' as table_name, COUNT(*) as count FROM agents WHERE id = 1152
UNION ALL
SELECT 'Instances', COUNT(*) FROM instances WHERE id = 1202
UNION ALL
SELECT 'Datacenters', COUNT(*) FROM datacenters WHERE id = 1101
UNION ALL
SELECT 'Regions', COUNT(*) FROM regions WHERE id = 3;
