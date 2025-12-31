export interface IStatusPageSettings {
  id?: number;
  statusPageId?: number;
  logoUrl?: string | null;
  themeColor?: string | null;
  customDomain?: string | null;
  headerText?: string | null;
  footerText?: string | null;
  showResponseTimes?: boolean | null;
  showUptimePercentage?: boolean | null;
  autoRefreshSeconds?: number | null;
  sampleSize?: number | null;
  successThresholdHigh?: number | null;
  successThresholdLow?: number | null;
  warningThresholdMs?: number | null;
  criticalThresholdMs?: number | null;
}

export const defaultValue: Readonly<IStatusPageSettings> = {
  showResponseTimes: true,
  showUptimePercentage: true,
  autoRefreshSeconds: 30,
  sampleSize: 20,
  successThresholdHigh: 0.8,
  successThresholdLow: 0.6,
  warningThresholdMs: 500,
  criticalThresholdMs: 1000,
};
