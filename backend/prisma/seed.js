import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

async function main() {
  // --- Seed Integrations (3 records) ---
  await prisma.integration.createMany({
    data: [
      {
        name: 'PostgreSQL',
        integrationType: 'Database',
        version: '15.4',
        description: 'PostgreSQL open source database',
        enabled: true
      },
      {
        name: 'Redis',
        integrationType: 'Cache',
        version: '7.2',
        description: 'Redis in-memory key-value store',
        enabled: true
      },
      {
        name: 'Nginx',
        integrationType: 'Gateway',
        version: '1.25',
        description: 'Nginx web server and reverse proxy',
        enabled: true
      }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Integrations`);

  // --- Seed Regions (3 records) ---
  await prisma.region.createMany({
    data: [
      { name: 'US-East' },
      { name: 'US-West' },
      { name: 'Europe-West' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Regions`);

  const allRegions = await prisma.region.findMany();

  // --- Seed Datacenters (3 records) ---
  await prisma.datacenter.createMany({
    data: allRegions.map((r, idx) => ({
      name: `${r.name} DC`,
      shortName: `dc${idx + 1}`,
      privateCIDR: `10.${idx}.0.0/16`,
      publicCIDR: `52.${idx}.0.0/16`,
      regionId: r.id
    })),
    skipDuplicates: true
  });
  console.log(`✅ Seeded Datacenters`);

  const allDatacenters = await prisma.datacenter.findMany();

  // --- Seed Environments (3 records) ---
  await prisma.environment.createMany({
    data: [
      { name: 'dev' },
      { name: 'stage' },
      { name: 'prod' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Environments`);
  const allEnvironments = await prisma.environment.findMany();

  // --- Seed Hosts (3 records) ---
  await prisma.host.createMany({
    data: [
      {
        datacenterId: allDatacenters[0].id,
        hostname: 'host1',
        privateIP: '10.0.1.10',
        publicIP: '10.0.1.20',
        kind: 'VM',
        tags: { os: 'ubuntu', env: 'prod' }
      },
      {
        datacenterId: allDatacenters[1].id,
        hostname: 'host2',
        privateIP: '10.0.2.10',
        publicIP: '10.0.2.20',
        kind: 'Physical',
        tags: { os: 'centos', env: 'stage' }
      },
      {
        datacenterId: allDatacenters[2].id,
        hostname: 'host3',
        privateIP: '10.0.3.10',
        publicIP: '10.0.3.20',
        kind: 'BareMetal',
        tags: { os: 'debian', env: 'dev' }
      }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Hosts`);

  // --- Seed Clusters (3 records) ---
  await prisma.cluster.createMany({
    data: [
      {
        name: 'cluster-dc1',
        environmentId: allEnvironments[0].id,
        datacenterId: allDatacenters[0].id
      },
      {
        name: 'cluster-dc2',
        environmentId: allEnvironments[1].id,
        datacenterId: allDatacenters[1].id
      },
      {
        name: 'cluster-dc3',
        environmentId: allEnvironments[2].id,
        datacenterId: allDatacenters[2].id
      }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Clusters`);

  const allHosts = await prisma.host.findMany();
  const allClusters = await prisma.cluster.findMany();
  const allIntegrations = await prisma.integration.findMany();

  // --- Seed IntegrationInstances (3 records) ---
  await prisma.integrationInstance.createMany({
    data: [
      {
        hostId: allHosts[0].id,
        datacenterId: allHosts[0].datacenterId,
        clusterId: allClusters[0].id,
        environmentId: allClusters[0].environmentId,
        integrationId: allIntegrations[0].id,
        port: 5432
      },
      {
        hostId: allHosts[1].id,
        datacenterId: allHosts[1].datacenterId,
        clusterId: allClusters[1].id,
        environmentId: allClusters[1].environmentId,
        integrationId: allIntegrations[1].id,
        port: 6379
      },
      {
        hostId: allHosts[2].id,
        datacenterId: allHosts[2].datacenterId,
        clusterId: allClusters[2].id,
        environmentId: allClusters[2].environmentId,
        integrationId: allIntegrations[2].id,
        port: 80
      }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded IntegrationInstances`);
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e);
    await prisma.$disconnect();
    process.exit(1);
  });
