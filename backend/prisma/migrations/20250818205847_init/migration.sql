-- CreateEnum
CREATE TYPE "public"."HostKind" AS ENUM ('VM', 'Physical', 'BareMetal');

-- CreateTable
CREATE TABLE "public"."Region" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "Region_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Team" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "Team_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceOrAppType" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,

    CONSTRAINT "ServiceOrAppType_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceCatalog" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "defaultPort" INTEGER,
    "description" TEXT,
    "serviceTypeId" INTEGER NOT NULL,

    CONSTRAINT "ServiceCatalog_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ApplicationCatalog" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "uniqueId" TEXT NOT NULL,
    "defaultPort" INTEGER,
    "description" TEXT,
    "appTypeId" INTEGER NOT NULL,
    "gitRepoUrl" TEXT,
    "teamId" INTEGER NOT NULL,

    CONSTRAINT "ApplicationCatalog_pkey" PRIMARY KEY ("id")
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
    "environment" TEXT,

    CONSTRAINT "Application_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Region_name_key" ON "public"."Region"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Team_name_key" ON "public"."Team"("name");

-- CreateIndex
CREATE UNIQUE INDEX "ServiceOrAppType_name_key" ON "public"."ServiceOrAppType"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Host_hostname_key" ON "public"."Host"("hostname");

-- AddForeignKey
ALTER TABLE "public"."ServiceCatalog" ADD CONSTRAINT "ServiceCatalog_serviceTypeId_fkey" FOREIGN KEY ("serviceTypeId") REFERENCES "public"."ServiceOrAppType"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ApplicationCatalog" ADD CONSTRAINT "ApplicationCatalog_teamId_fkey" FOREIGN KEY ("teamId") REFERENCES "public"."Team"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ApplicationCatalog" ADD CONSTRAINT "ApplicationCatalog_appTypeId_fkey" FOREIGN KEY ("appTypeId") REFERENCES "public"."ServiceOrAppType"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Datacenter" ADD CONSTRAINT "Datacenter_regionId_fkey" FOREIGN KEY ("regionId") REFERENCES "public"."Region"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Host" ADD CONSTRAINT "Host_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_hostId_fkey" FOREIGN KEY ("hostId") REFERENCES "public"."Host"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Service" ADD CONSTRAINT "Service_catalogId_fkey" FOREIGN KEY ("catalogId") REFERENCES "public"."ServiceCatalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_hostId_fkey" FOREIGN KEY ("hostId") REFERENCES "public"."Host"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_catalogId_fkey" FOREIGN KEY ("catalogId") REFERENCES "public"."ApplicationCatalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Application" ADD CONSTRAINT "Application_teamId_fkey" FOREIGN KEY ("teamId") REFERENCES "public"."Team"("id") ON DELETE SET NULL ON UPDATE CASCADE;
