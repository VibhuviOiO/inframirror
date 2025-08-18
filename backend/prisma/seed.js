import { PrismaClient } from '@prisma/client'

const prisma = new PrismaClient()

async function main() {
  // Seed Teams (ensure before ApplicationCatalog)
  await prisma.team.createMany({
    data: [
      { name: 'Platform' },
      { name: 'DevOps' },
      { name: 'SRE' }
    ],
    skipDuplicates: true
  });
  console.log("✅ Team seed data inserted successfully");

  // Seed Regions (ensure before Datacenter)
  await prisma.region.createMany({
    data: [
      { name: 'US-East' },
      { name: 'Europe-West' },
      { name: 'Asia-South' }
    ],
    skipDuplicates: true
  });
  console.log("✅ Region seed data inserted successfully");

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
    console.log("✅ Datacenter seed data inserted successfully");
  }
  const allDatacenters = await prisma.datacenter.findMany();

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
  console.log("✅ Host seed data inserted successfully");

  // Seed ServiceOrAppType
  await prisma.serviceOrAppType.createMany({
    data: [
      { name: 'Database' },
      { name: 'Cache' },
      { name: 'Web Server' }
    ],
    skipDuplicates: true
  });
  console.log("✅ ServiceOrAppType seed data inserted successfully");

  // Seed ServiceCatalog
  const allTypes = await prisma.serviceOrAppType.findMany();
  const typeDb = allTypes.find(t => t.name === 'Database');
  const typeCache = allTypes.find(t => t.name === 'Cache');
  let allServiceCatalogs = [];
  if (typeDb && typeCache) {
    await prisma.serviceCatalog.createMany({
      data: [
        {
          name: 'PostgreSQL',
          defaultPort: 5432,
          description: 'Open source relational database',
          serviceTypeId: typeDb.id
        },
        {
          name: 'Redis',
          defaultPort: 6379,
          description: 'In-memory cache',
          serviceTypeId: typeCache.id
        }
      ],
      skipDuplicates: true
    });
    console.log("✅ ServiceCatalog seed data inserted successfully");
  }
  allServiceCatalogs = await prisma.serviceCatalog.findMany();

  // Seed ApplicationCatalog
  const allAppTypes = await prisma.serviceOrAppType.findMany();
  const allTeams = await prisma.team.findMany();
  if (allAppTypes.length && allTeams.length) {
    await prisma.applicationCatalog.createMany({
      data: [
        {
          name: 'Demo App',
          uniqueId: 'app-001',
          appTypeId: allAppTypes[0].id,
          teamId: allTeams[0].id
        },
        {
          name: 'Demo App 2',
          uniqueId: 'app-002',
          appTypeId: allAppTypes[0].id,
          teamId: allTeams[0].id
        }
      ],
      skipDuplicates: true
    });
    console.log("✅ ApplicationCatalog seed data inserted successfully");
  }
  const allCatalogs = await prisma.applicationCatalog.findMany();

  // Seed Environments
  await prisma.environment.createMany({
    data: [
      { name: 'dev' },
      { name: 'stage' },
      { name: 'prod' }
    ],
    skipDuplicates: true
  });
  console.log("✅ Environment seed data inserted successfully");
  const allEnvironments = await prisma.environment.findMany();

  // Seed Applications (one per datacenter, catalog, and environment)
  for (const dc of allDatacenters) {
    for (const cat of allCatalogs) {
      for (const env of allEnvironments) {
        await prisma.application.create({
          data: {
            datacenterId: dc.id,
            catalogId: cat.id,
            environmentId: env.id
          }
        });
      }
    }
  }
  console.log("✅ Application seed data inserted successfully");

  // Seed Services (one per host, using first ServiceCatalog)
  const allHosts = await prisma.host.findMany();
  if (allServiceCatalogs.length && allHosts.length) {
    for (const host of allHosts) {
      await prisma.service.create({
        data: {
          datacenterId: host.datacenterId,
          hostId: host.id,
          catalogId: allServiceCatalogs[0].id
        }
      });
    }
    console.log("✅ Service seed data inserted successfully");
  }
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e)
    await prisma.$disconnect()
    process.exit(1)
  })
