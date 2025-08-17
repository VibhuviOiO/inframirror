import { PrismaClient } from '@prisma/client'

const prisma = new PrismaClient()

async function main() {
  // --- Datacenters ---
  const dc1 = await prisma.datacenter.create({
    data: {
      name: "Primary DC",
      shortName: "DC1",
      privateCIDR: "10.0.0.0/16",
      publicCIDR: "203.0.113.0/24"
    }
  })

  // --- Service Types ---
  const computeType = await prisma.serviceType.create({
    data: {
      name: "Compute",
      description: "Virtual machines, nodes, etc."
    }
  })

  const storageType = await prisma.serviceType.create({
    data: {
      name: "Storage",
      description: "Databases, object stores"
    }
  })

  // --- Service Catalog ---
  const postgresCatalog = await prisma.serviceCatalog.create({
    data: {
      name: "PostgreSQL",
      description: "Relational database service",
      defaultType: { connect: { id: storageType.id } }
    }
  })

  const nginxCatalog = await prisma.serviceCatalog.create({
    data: {
      name: "NGINX",
      description: "Load balancer and reverse proxy",
      defaultType: { connect: { id: computeType.id } }
    }
  })

  // --- Datacenter Services ---
  const postgresService = await prisma.datacenterService.create({
    data: {
      datacenterId: dc1.id,
      serviceCatalogId: postgresCatalog.id,
      serviceTypeId: storageType.id,
      config: { version: "16.1", ha: true }
    }
  })

  const nginxService = await prisma.datacenterService.create({
    data: {
      datacenterId: dc1.id,
      serviceCatalogId: nginxCatalog.id,
      serviceTypeId: computeType.id,
      config: { replicas: 2 }
    }
  })

  // --- Service Instances ---
  await prisma.serviceInstance.createMany({
    data: [
      {
        datacenterServiceId: postgresService.id,
        ipAddress: "10.0.1.10",
        port: 5432,
        metadata: { role: "primary" }
      },
      {
        datacenterServiceId: postgresService.id,
        ipAddress: "10.0.1.11",
        port: 5432,
        metadata: { role: "replica" }
      }
    ]
  })

  // --- Applications ---
  const app1 = await prisma.application.create({
    data: {
      name: "Billing Service",
      description: "Handles invoices and payments",
      ownerTeam: "FinTech",
      gitRepoUrl: "https://github.com/example/billing-service"
    }
  })

  // --- App Deployment ---
  await prisma.appDeployment.create({
    data: {
      applicationId: app1.id,
      datacenterId: dc1.id,
      serviceId: postgresService.id,
      version: "v1.0.0",
      config: { migration: "2025-08-17" }
    }
  })

  // --- Service Owners ---
  await prisma.serviceOwner.create({
    data: {
      serviceCatalogId: postgresCatalog.id,
      teamName: "DBA Team",
      contactEmail: "dba@example.com",
      slackChannel: "#db-alerts"
    }
  })

  console.log("✅ Seed data inserted successfully")
}

main()
  .then(() => prisma.$disconnect())
  .catch(async (e) => {
    console.error(e)
    await prisma.$disconnect()
    process.exit(1)
  })
