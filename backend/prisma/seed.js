import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

async function main() {
  // --- Seed Teams ---
  await prisma.team.createMany({
    data: [
      { name: 'Platform' },
      { name: 'DevOps' },
      { name: 'SRE' },
      { name: 'Networking' },
      { name: 'Security' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Teams`);

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

  // --- Seed Catalog Types ---
  await prisma.catalogType.createMany({
    data: [
      { name: 'Database', description: 'DBs' },
      { name: 'Cache', description: 'Caching systems' },
      { name: 'Web', description: 'Web servers' },
      { name: 'Messaging', description: 'MQs and brokers' },
      { name: 'Storage', description: 'Blob/file stores' }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded CatalogTypes`);

  const allTypes = await prisma.catalogType.findMany();
  const allTeams = await prisma.team.findMany();

  // --- Seed Catalogs ---
  await prisma.catalog.createMany({
    data: [
      { name: 'PostgreSQL', uniqueId: 'pg-001', defaultPort: 5432, catalogTypeId: (allTypes.find(t => t.name === 'Database') || {}).id, teamId: allTeams[0].id },
      { name: 'Redis', uniqueId: 'redis-001', defaultPort: 6379, catalogTypeId: (allTypes.find(t => t.name === 'Cache') || {}).id, teamId: allTeams[1].id },
      { name: 'Nginx', uniqueId: 'nginx-001', defaultPort: 80, catalogTypeId: (allTypes.find(t => t.name === 'Web') || {}).id, teamId: allTeams[2].id },
      { name: 'Kafka', uniqueId: 'kafka-001', defaultPort: 9092, catalogTypeId: (allTypes.find(t => t.name === 'Messaging') || {}).id, teamId: allTeams[3].id },
      { name: 'MinIO', uniqueId: 'minio-001', defaultPort: 9000, catalogTypeId: (allTypes.find(t => t.name === 'Storage') || {}).id, teamId: allTeams[4].id }
    ],
    skipDuplicates: true
  });
  console.log(`✅ Seeded Catalogs`);

  const allCatalogs = await prisma.catalog.findMany(); // ⚡ moved up

  // --- Seed Datacenters ---
  const allRegions = await prisma.region.findMany();
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

  // --- Seed Environments --- ⚡ moved up before clusters
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

  // --- Seed Clusters ---
  for (let i = 0; i < allDatacenters.length; i++) {
    await prisma.cluster.create({
      data: {
        name: `cluster-${allDatacenters[i].shortName}`,
        catalogId: allCatalogs[i % allCatalogs.length].id,
        environmentId: allEnvironments[i % allEnvironments.length].id,
        datacenterId: allDatacenters[i].id
      }
    });
  }
  console.log(`✅ Seeded Clusters`);

  // --- Seed Hosts ---
  for (const dc of allDatacenters) {
    await prisma.host.create({
      data: {
        datacenterId: dc.id,
        hostname: `host-${dc.shortName}`,
        privateIP: `10.${dc.id}.0.10`,
        publicIP: `52.${dc.id}.0.10`,
        kind: 'VM',
        tags: { role: 'seed' }
      }
    });
  }
  console.log(`✅ Seeded Hosts`);

  // --- Seed Services ---
  const allHosts = await prisma.host.findMany();
  for (let i = 0; i < allHosts.length; i++) {
    await prisma.service.create({
      data: {
        datacenterId: allHosts[i].datacenterId,
        hostId: allHosts[i].id,
        catalogId: allCatalogs[i % allCatalogs.length].id,
        metadata: { version: "1.0.0" }
      }
    });
  }
  console.log(`✅ Seeded Services`);
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e);
    await prisma.$disconnect();
    process.exit(1);
  });
