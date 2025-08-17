-- CreateTable
CREATE TABLE "public"."Datacenter" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "shortName" TEXT NOT NULL,
    "privateCIDR" TEXT,
    "publicCIDR" TEXT,

    CONSTRAINT "Datacenter_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceType" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,

    CONSTRAINT "ServiceType_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceCatalog" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "defaultTypeId" INTEGER,

    CONSTRAINT "ServiceCatalog_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."DatacenterService" (
    "id" SERIAL NOT NULL,
    "datacenterId" INTEGER NOT NULL,
    "serviceCatalogId" INTEGER NOT NULL,
    "serviceTypeId" INTEGER NOT NULL,
    "isEnabled" BOOLEAN NOT NULL DEFAULT true,
    "config" JSONB NOT NULL,

    CONSTRAINT "DatacenterService_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceInstance" (
    "id" SERIAL NOT NULL,
    "datacenterServiceId" INTEGER NOT NULL,
    "ipAddress" TEXT NOT NULL,
    "port" INTEGER NOT NULL,
    "metadata" JSONB NOT NULL,

    CONSTRAINT "ServiceInstance_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."Application" (
    "id" SERIAL NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "ownerTeam" TEXT,
    "gitRepoUrl" TEXT,

    CONSTRAINT "Application_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."AppDeployment" (
    "id" SERIAL NOT NULL,
    "applicationId" INTEGER NOT NULL,
    "datacenterId" INTEGER NOT NULL,
    "serviceId" INTEGER NOT NULL,
    "version" TEXT,
    "config" JSONB NOT NULL,

    CONSTRAINT "AppDeployment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceOwner" (
    "id" SERIAL NOT NULL,
    "serviceCatalogId" INTEGER NOT NULL,
    "teamName" TEXT NOT NULL,
    "contactEmail" TEXT,
    "slackChannel" TEXT,

    CONSTRAINT "ServiceOwner_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "public"."ServiceDependency" (
    "id" SERIAL NOT NULL,
    "fromServiceId" INTEGER NOT NULL,
    "toServiceId" INTEGER NOT NULL,
    "relationType" TEXT NOT NULL,

    CONSTRAINT "ServiceDependency_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "ServiceType_name_key" ON "public"."ServiceType"("name");

-- CreateIndex
CREATE UNIQUE INDEX "ServiceCatalog_name_key" ON "public"."ServiceCatalog"("name");

-- CreateIndex
CREATE UNIQUE INDEX "Application_name_key" ON "public"."Application"("name");

-- AddForeignKey
ALTER TABLE "public"."ServiceCatalog" ADD CONSTRAINT "ServiceCatalog_defaultTypeId_fkey" FOREIGN KEY ("defaultTypeId") REFERENCES "public"."ServiceType"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."DatacenterService" ADD CONSTRAINT "DatacenterService_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."DatacenterService" ADD CONSTRAINT "DatacenterService_serviceCatalogId_fkey" FOREIGN KEY ("serviceCatalogId") REFERENCES "public"."ServiceCatalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."DatacenterService" ADD CONSTRAINT "DatacenterService_serviceTypeId_fkey" FOREIGN KEY ("serviceTypeId") REFERENCES "public"."ServiceType"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ServiceInstance" ADD CONSTRAINT "ServiceInstance_datacenterServiceId_fkey" FOREIGN KEY ("datacenterServiceId") REFERENCES "public"."DatacenterService"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."AppDeployment" ADD CONSTRAINT "AppDeployment_applicationId_fkey" FOREIGN KEY ("applicationId") REFERENCES "public"."Application"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."AppDeployment" ADD CONSTRAINT "AppDeployment_datacenterId_fkey" FOREIGN KEY ("datacenterId") REFERENCES "public"."Datacenter"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."AppDeployment" ADD CONSTRAINT "AppDeployment_serviceId_fkey" FOREIGN KEY ("serviceId") REFERENCES "public"."DatacenterService"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ServiceOwner" ADD CONSTRAINT "ServiceOwner_serviceCatalogId_fkey" FOREIGN KEY ("serviceCatalogId") REFERENCES "public"."ServiceCatalog"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ServiceDependency" ADD CONSTRAINT "ServiceDependency_fromServiceId_fkey" FOREIGN KEY ("fromServiceId") REFERENCES "public"."DatacenterService"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "public"."ServiceDependency" ADD CONSTRAINT "ServiceDependency_toServiceId_fkey" FOREIGN KEY ("toServiceId") REFERENCES "public"."DatacenterService"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
