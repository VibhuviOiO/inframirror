import prisma from './prismaClient.js';

export async function seedDatabase() {
  // --- Seed Integrations ---
  await prisma.integration.createMany({
    data: [
      { name: 'PostgreSQL', integrationType: 'Database', version: '15.4', description: 'PostgreSQL open source database', enabled: true },
      { name: 'Redis', integrationType: 'Cache', version: '7.2', description: 'Redis in-memory key-value store', enabled: true },
      { name: 'Elasticsearch', integrationType: 'SearchEngine', version: '8.11', description: 'Elasticsearch search engine', enabled: false },
      { name: 'RabbitMQ', integrationType: 'OrchestrationFramework', version: '3.12', description: 'RabbitMQ message broker', enabled: false },
      { name: 'Nginx', integrationType: 'Gateway', version: '1.25', description: 'Nginx web server and reverse proxy', enabled: true }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Integrations`);

  // --- Seed Regions ---
  await prisma.region.createMany({
    data: [
      { name: 'US-East' },
      { name: 'US-West' },
      { name: 'Europe-West' },
      { name: 'Asia-South' },
      { name: 'Australia-East' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Regions`);

  const allRegions = await prisma.region.findMany();

  // --- Seed Datacenters ---
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

  // --- Seed Environments ---
  await prisma.environment.createMany({
    data: [
      { name: 'dev' },
      { name: 'stage' },
      { name: 'qa' },
      { name: 'uat' },
      { name: 'prod' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Environments`);
  const allEnvironments = await prisma.environment.findMany();

  // --- Seed Hosts ---
  let hostCount = 1;
  for (const dc of allDatacenters) {
    await prisma.host.create({
      data: { datacenterId: dc.id, hostname: `host${hostCount}`, privateIP: `10.0.${hostCount}.10`, publicIP: `10.0.${hostCount}.20`, kind: 'VM', tags: { os: 'ubuntu', env: 'prod' } }
    });
    hostCount++;
    await prisma.host.create({
      data: { datacenterId: dc.id, hostname: `host${hostCount}`, privateIP: `10.0.${hostCount}.10`, publicIP: `10.0.${hostCount}.20`, kind: 'Physical', tags: { os: 'centos', env: 'stage' } }
    });
    hostCount++;
  }
  console.log(`✅ Seeded Hosts`);

  // --- Seed Clusters ---
  for (let i = 0; i < allDatacenters.length; i++) {
    await prisma.cluster.create({
      data: {
        name: `cluster-${allDatacenters[i].shortName}`,
        environmentId: allEnvironments[i % allEnvironments.length].id,
        datacenterId: allDatacenters[i].id
      }
    });
  }
  console.log(`✅ Seeded Clusters`);

  const allHosts = await prisma.host.findMany();
  const allClusters = await prisma.cluster.findMany();
  const allIntegrations = await prisma.integration.findMany();

  // --- Seed IntegrationInstances ---
  for (let i = 0; i < allHosts.length; i++) {
    await prisma.integrationInstance.create({
      data: {
        hostId: allHosts[i].id,
        datacenterId: allHosts[i].datacenterId,
        clusterId: allClusters[i % allClusters.length]?.id,
        environmentId: allClusters[i % allClusters.length]?.environmentId,
        integrationId: allIntegrations[i % allIntegrations.length]?.id,
        port: 7000 + i
      }
    });
  }
  console.log(`✅ Seeded IntegrationInstances`);
}


// Only run seeding if this file is executed directly (not when imported)
if (import.meta.url === process.argv[1] || import.meta.url.endsWith('seed.js')) {
  if (process.env.SEED_DATA === 'true') {
    seedDatabase()
      .then(() => prisma.$disconnect())
      .catch(async (e: unknown) => {
        console.error(e);
        await prisma.$disconnect();
        process.exit(1);
      });
  } else {
    console.log('SEED_DATA is not true, skipping seeding.');
    prisma.$disconnect();
  }
}
