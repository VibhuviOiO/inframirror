/*
  Warnings:

  - You are about to drop the column `enabled` on the `IntegrationInstance` table. All the data in the column will be lost.

*/
-- AlterTable
ALTER TABLE "public"."IntegrationInstance" DROP COLUMN "enabled";
