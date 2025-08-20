import { useLocation } from "react-router-dom";

const PAGE_TITLES: Record<string, string> = {
  "/": "Dashboard",
  "/datacenters": "Datacenters",
  "/catalog": "Catalog",
  "/service-catalogs": "Service Catalogs",
  "/application-catalogs": "Application Catalogs",
  "/applications": "Applications",
  "/applications/deployments": "Deployments",
  "/infrastructure/datacenters/enabled-services": "Enabled Services",
  "/infrastructure/datacenters/instances": "Datacenter Instances",
  // Add more as needed
};

export function usePageTitle() {
  const { pathname } = useLocation();
  // Try to match the full path, fallback to first segment
  return (
    PAGE_TITLES[pathname] ||
    PAGE_TITLES["/" + pathname.split("/")[1]] ||
    ""
  );
}
