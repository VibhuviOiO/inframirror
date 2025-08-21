import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

async function main() {
  // Seed Teams
  await prisma.team.createMany({
    data: [
      { name: 'Platform' },
      { name: 'DevOps' },
      { name: 'SRE' }
    ],
    skipDuplicates: true
  });
  const teams = await prisma.team.findMany();
  console.log(`✅ Team seed data inserted successfully (${teams.length})`);

  // Seed Regions
  await prisma.region.createMany({
    data: [
      { name: 'US-East' },
      { name: 'Europe-West' },
      { name: 'Asia-South' }
    ],
    skipDuplicates: true
  });
  const regions = await prisma.region.findMany();
  console.log(`✅ Region seed data inserted successfully (${regions.length})`);

  // Seed CatalogTypes
  await prisma.catalogType.createMany({
    data: [
      { name: 'Database', description: 'DBs' },
      { name: 'Cache', description: 'Caching systems' },
      { name: 'Web', description: 'Web servers' }
    ],
    skipDuplicates: true
  });
  const catalogTypes = await prisma.catalogType.findMany();
  console.log(`✅ CatalogType seed data inserted successfully (${catalogTypes.length})`);

  // Seed Catalogs
  const allTypes = await prisma.catalogType.findMany();
  const allTeams = await prisma.team.findMany();
  const typeDb = allTypes.find(t => t.name === 'Database');
  const typeCache = allTypes.find(t => t.name === 'Cache');
  let allCatalogs = [];
  if (typeDb && typeCache && allTeams.length) {
    await prisma.catalog.createMany({
      data: [
        {
          name: 'PostgreSQL',
          uniqueId: 'pg-001',
          defaultPort: 5432,
          description: 'Open source relational database',
          catalogTypeId: typeDb.id,
          teamId: allTeams[0].id
        },
        {
          name: 'Redis',
          uniqueId: 'redis-001',
          defaultPort: 6379,
          description: 'In-memory cache',
          catalogTypeId: typeCache.id,
          teamId: allTeams[1].id
        }
      ],
      skipDuplicates: true
    });
    // ...existing code...
  }
  allCatalogs = await prisma.catalog.findMany();
  console.log(`✅ Catalog seed data inserted successfully (${allCatalogs.length})`);

  // Seed Datacenters
  const allRegions = await prisma.region.findMany();
  const regionUsEast = allRegions.find(r => r.name === 'US-East');
  const regionEuropeWest = allRegions.find(r => r.name === 'Europe-West');
  if (regionUsEast && regionEuropeWest) {
    await prisma.datacenter.createMany({
      data: [
        {
          name: 'Ashburn DC',
          shortName: 'ash',
          privateCIDR: '10.0.0.0/16',
          publicCIDR: '52.0.0.0/16',
          regionId: regionUsEast.id
        },
        {
          name: 'Frankfurt DC',
          shortName: 'fra',
          privateCIDR: '10.1.0.0/16',
          publicCIDR: '52.1.0.0/16',
          regionId: regionEuropeWest.id
        }
      ],
      skipDuplicates: true
    });
    // ...existing code...
  }
  const allDatacenters = await prisma.datacenter.findMany();
  console.log(`✅ Datacenter seed data inserted successfully (${allDatacenters.length})`);

  // Seed Hosts (one per datacenter)
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
  const hosts = await prisma.host.findMany();
  console.log(`✅ Host seed data inserted successfully (${hosts.length})`);

  // Seed Environments
  await prisma.environment.createMany({
    data: [
      { name: 'dev' },
      { name: 'stage' },
      { name: 'prod' }
    ],
    skipDuplicates: true
  });
  const environments = await prisma.environment.findMany();
  console.log(`✅ Environment seed data inserted successfully (${environments.length})`);
  const allEnvironments = environments;

  // Seed Applications (one per datacenter, catalog, and environment)
  for (const dc of allDatacenters) {
    for (const cat of allCatalogs) {
      for (const env of allEnvironments) {
        await prisma.application.create({
          data: {
            datacenterId: dc.id,
            catalogId: cat.id,
            environmentId: env.id,
            teamId: cat.teamId
          }
        });
      }
    }
  }
  const applications = await prisma.application.findMany();
  console.log(`✅ Application seed data inserted successfully (${applications.length})`);

  // Seed Services (one per host, using first Catalog)
  const allHosts = await prisma.host.findMany();
  if (allCatalogs.length && allHosts.length) {
    for (const host of allHosts) {
      await prisma.service.create({
        data: {
          datacenterId: host.datacenterId,
          hostId: host.id,
          catalogId: allCatalogs[0].id
        }
      });
    }
    const services = await prisma.service.findMany();
  console.log(`✅ Service seed data inserted successfully (${services.length})`);
  }
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e);
    await prisma.$disconnect();
    process.exit(1);
  });
