export interface IHttpMonitor {
  id?: number;
  name?: string;
  method?: string;
  type?: string;
  url?: string | null;
  headers?: string | null;
  body?: string | null;
  intervalSeconds?: number;
  timeoutSeconds?: number;
  retryCount?: number;
  retryDelaySeconds?: number;
  responseTimeWarningMs?: number | null;
  responseTimeCriticalMs?: number | null;
  uptimeWarningPercent?: number | null;
  uptimeCriticalPercent?: number | null;
  includeResponseBody?: boolean | null;
  resendNotificationCount?: number | null;
  certificateExpiryDays?: number | null;
  ignoreTlsError?: boolean | null;
  checkSslCertificate?: boolean | null;
  checkDnsResolution?: boolean | null;
  upsideDownMode?: boolean | null;
  maxRedirects?: number | null;
  description?: string | null;
  tags?: string | null;
  enabled?: boolean | null;
  expectedStatusCodes?: string | null;
  performanceBudgetMs?: number | null;
  sizeBudgetKb?: number | null;
  parent?: IHttpMonitor | null;
}

export const defaultValue: Readonly<IHttpMonitor> = {
  includeResponseBody: false,
  ignoreTlsError: false,
  checkSslCertificate: false,
  checkDnsResolution: false,
  upsideDownMode: false,
  enabled: false,
};
