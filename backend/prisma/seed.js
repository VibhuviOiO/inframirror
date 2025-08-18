import { PrismaClient } from '@prisma/client'

const prisma = new PrismaClient()

async function main() {

  await prisma.team.createMany({
    data: [
      { name: 'Platform' },
      { name: 'DevOps' },
      { name: 'SRE' }
    ],
    skipDuplicates: true
  });
  console.log("✅ Team seed data inserted successfully");

  await prisma.region.createMany({
    data: [
      { name: 'US-East' },
      { name: 'Europe-West' },
      { name: 'Asia-South' }
    ],
    skipDuplicates: true
  });

  const regions = await prisma.region.findMany();
  const usEast = regions.find(r => r.name === 'US-East');
  const europeWest = regions.find(r => r.name === 'Europe-West');

  if (usEast && europeWest) {
    await prisma.datacenter.createMany({
      data: [
        {
          name: 'Ashburn DC',
          shortName: 'ash',
          privateCIDR: '10.0.0.0/16',
          publicCIDR: '52.0.0.0/16',
          regionId: usEast.id
        },
        {
          name: 'Frankfurt DC',
          shortName: 'fra',
          privateCIDR: '10.1.0.0/16',
          publicCIDR: '52.1.0.0/16',
          regionId: europeWest.id
        }
      ],
      skipDuplicates: true
    });
    console.log("✅ Datacenter seed data inserted successfully");
  }

  console.log("✅ Region seed data inserted successfully")
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e)
    await prisma.$disconnect()
    process.exit(1)
  })
