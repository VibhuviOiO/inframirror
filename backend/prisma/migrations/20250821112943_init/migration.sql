-- CreateEnum
CREATE TYPE "public"."HostKind" AS ENUM ('VM', 'Physical', 'BareMetal');

-- CreateTable
CREATE TABLE "public"."Region" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "Region_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Environment" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "Environment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Team" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "Team_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."CatalogType" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,

    CONSTRAINT "CatalogType_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Catalog" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "uniqueId" TEXT,
    "defaultPort" INTEGER,
    "description" TEXT,
    "gitRepoUrl" TEXT,
    "teamId" INTEGER,
    "catalogTypeId" INTEGER NOT NULL,

    CONSTRAINT "Catalog_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Datacenter" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "shortName" TEXT NOT NULL,
    "privateCIDR" TEXT,
    "publicCIDR" TEXT,
    "regionId" INTEGER NOT NULL,

    CONSTRAINT "Datacenter_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Host" (
    "id" SERIAL NOT NULL,
    "datacenterId" INTEGER NOT NULL,
    "hostname" TEXT NOT NULL,
    "privateIP" TEXT NOT NULL,
    "publicIP" TEXT,
    "kind" "public"."HostKind" NOT NULL,
    "tags" JSONB,

    CONSTRAINT "Host_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Service" (
    "id" SERIAL NOT NULL,
    "datacenterId" INTEGER NOT NULL,
    "hostId" INTEGER NOT NULL,
    "catalogId" INTEGER NOT NULL,

    CONSTRAINT "Service_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Application" (
    "id" SERIAL NOT NULL,
    "datacenterId" INTEGER NOT NULL,
    "hostId" INTEGER,
    "catalogId" INTEGER NOT NULL,
    "teamId" INTEGER,
    "environmentId" INTEGER,

    CONSTRAINT "Application_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Region_name_key" ON "public"."Region"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Environment_name_key" ON "public"."Environment"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Team_name_key" ON "public"."Team"("name");

-- CreateIndex
CREATE UNIQUE INDEX "CatalogType_name_key" ON "public"."CatalogType"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Catalog_uniqueId_key" ON "public"."Catalog"("uniqueId");

-- CreateIndex
CREATE UNIQUE INDEX "Host_hostname_key" ON "public"."Host"("hostname");

-- AddForeignKey
ALTER TABLE "public"."Catalog" ADD CONSTRAINT "Catalog_teamId_fkey" FOREIGN KEY ("teamId") REFERENCES "public"."Team"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Catalog" ADD CONSTRAINT "Catalog_catalogTypeId_fkey" FOREIGN KEY ("catalogTypeId") REFERENCES "public"."CatalogType"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Datacenter" ADD CONSTRAINT "Datacenter_regionId_fkey" FOREIGN KEY ("regionId") REFERENCES "public"."Region"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Host" ADD CONSTRAINT "Host_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_hostId_fkey" FOREIGN KEY ("hostId") REFERENCES "public"."Host"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_catalogId_fkey" FOREIGN KEY ("catalogId") REFERENCES "public"."Catalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_hostId_fkey" FOREIGN KEY ("hostId") REFERENCES "public"."Host"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_catalogId_fkey" FOREIGN KEY ("catalogId") REFERENCES "public"."Catalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_teamId_fkey" FOREIGN KEY ("teamId") REFERENCES "public"."Team"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_environmentId_fkey" FOREIGN KEY ("environmentId") REFERENCES "public"."Environment"("id") ON DELETE SET NULL ON UPDATE CASCADE;
