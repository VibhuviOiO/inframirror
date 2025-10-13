-- AlterTable
ALTER TABLE "public"."monitors" ADD COLUMN     "criticalThresholdMs" INTEGER,
ADD COLUMN     "warningThresholdMs" INTEGER;
