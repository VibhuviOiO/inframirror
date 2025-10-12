-- CreateEnum
CREATE TYPE "public"."IntegrationType" AS ENUM ('Database', 'KeyValueStore', 'SearchEngine', 'Cache', 'OrchestrationFramework', 'Container', 'Gateway');

-- CreateEnum
CREATE TYPE "public"."HostKind" AS ENUM ('VM', 'Physical', 'BareMetal');

-- CreateEnum
CREATE TYPE "public"."MonitorType" AS ENUM ('HTTP', 'TCP', 'UDP', 'PING', 'DNS');

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
CREATE TABLE "public"."Cluster" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "environmentId" INTEGER,
    "datacenterId" INTEGER,

    CONSTRAINT "Cluster_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Integration" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "integrationType" "public"."IntegrationType" NOT NULL,
    "version" TEXT NOT NULL,
    "description" TEXT,
    "updatedAt" TIMESTAMP(3) NOT NULL,
    "enabled" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "Integration_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."IntegrationInstance" (
    "id" SERIAL NOT NULL,
    "datacenterId" INTEGER,
    "hostId" INTEGER NOT NULL,
    "clusterId" INTEGER,
    "environmentId" INTEGER,
    "integrationId" INTEGER NOT NULL,
    "port" INTEGER,
    "config" JSONB,

    CONSTRAINT "IntegrationInstance_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."monitors" (
    "id" SERIAL NOT NULL,
    "monitorId" TEXT NOT NULL,
    "monitorName" TEXT,
    "monitorType" "public"."MonitorType" NOT NULL,
    "targetHost" TEXT NOT NULL,
    "targetPort" INTEGER,
    "targetPath" TEXT,
    "httpMethod" TEXT,
    "expectedStatusCode" INTEGER,
    "dnsQueryType" TEXT,
    "dnsExpectedResponse" TEXT,
    "executedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "agentId" TEXT,
    "agentRegion" TEXT,
    "success" BOOLEAN NOT NULL,
    "responseTime" INTEGER,
    "responseSizeBytes" INTEGER,
    "responseStatusCode" INTEGER,
    "responseContentType" TEXT,
    "responseServer" TEXT,
    "responseCacheStatus" TEXT,
    "dnsLookupMs" INTEGER,
    "tcpConnectMs" INTEGER,
    "tlsHandshakeMs" INTEGER,
    "timeToFirstByteMs" INTEGER,
    "packetLoss" DOUBLE PRECISION,
    "jitterMs" INTEGER,
    "dnsResponseValue" TEXT,
    "errorMessage" TEXT,
    "errorType" TEXT,
    "rawResponseHeaders" JSONB,
    "rawResponseBody" TEXT,
    "rawRequestHeaders" JSONB,
    "rawNetworkData" JSONB,

    CONSTRAINT "monitors_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Region_name_key" ON "public"."Region"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Environment_name_key" ON "public"."Environment"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Host_hostname_key" ON "public"."Host"("hostname");

-- CreateIndex
CREATE UNIQUE INDEX "Integration_name_key" ON "public"."Integration"("name");

-- CreateIndex
CREATE INDEX "monitors_monitorId_idx" ON "public"."monitors"("monitorId");

-- CreateIndex
CREATE INDEX "monitors_executedAt_idx" ON "public"."monitors"("executedAt" DESC);

-- CreateIndex
CREATE INDEX "monitors_success_idx" ON "public"."monitors"("success");

-- CreateIndex
CREATE INDEX "monitors_monitorType_idx" ON "public"."monitors"("monitorType");

-- CreateIndex
CREATE INDEX "monitors_targetHost_idx" ON "public"."monitors"("targetHost");

-- CreateIndex
CREATE INDEX "monitors_responseTime_idx" ON "public"."monitors"("responseTime" DESC);

-- CreateIndex
CREATE INDEX "monitors_agentRegion_idx" ON "public"."monitors"("agentRegion");

-- CreateIndex
CREATE INDEX "monitors_errorType_idx" ON "public"."monitors"("errorType");

-- AddForeignKey
ALTER TABLE "public"."Datacenter" ADD CONSTRAINT "Datacenter_regionId_fkey" FOREIGN KEY ("regionId") REFERENCES "public"."Region"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Host" ADD CONSTRAINT "Host_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Cluster" ADD CONSTRAINT "Cluster_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."Cluster" ADD CONSTRAINT "Cluster_environmentId_fkey" FOREIGN KEY ("environmentId") REFERENCES "public"."Environment"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."IntegrationInstance" ADD CONSTRAINT "IntegrationInstance_clusterId_fkey" FOREIGN KEY ("clusterId") REFERENCES "public"."Cluster"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."IntegrationInstance" ADD CONSTRAINT "IntegrationInstance_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."IntegrationInstance" ADD CONSTRAINT "IntegrationInstance_environmentId_fkey" FOREIGN KEY ("environmentId") REFERENCES "public"."Environment"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."IntegrationInstance" ADD CONSTRAINT "IntegrationInstance_hostId_fkey" FOREIGN KEY ("hostId") REFERENCES "public"."Host"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."IntegrationInstance" ADD CONSTRAINT "IntegrationInstance_integrationId_fkey" FOREIGN KEY ("integrationId") REFERENCES "public"."Integration"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
