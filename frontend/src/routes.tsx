import Index from "./pages/Index";
import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import Applications from "./pages/Applications";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import { CatalogPageWithLayout } from "./pages/Catalog";
import ServiceOwners from "./pages/ServiceOwners";
import ServiceDependencies from "./pages/ServiceDependencies";
import EnvironmentsPage from "./pages/Environments";
import TeamsPage from "./pages/Teams";
import CatalogTypePage from "./pages/CatalogType";
import HostsPage from "./pages/Hosts";

const routes = [
  {
    path: "/",
    element: <Index />,
  },
  {
    path: "/datacenters",
    element: <DatacentersPage />,
  },
  { path: "/catalog", element: <CatalogPageWithLayout /> },
  { path: "/hosts", element: <HostsPage /> },
  {
    path: "/settings",
    element: <SettingsPage />,
    children: [
      { path: "regions", element: <RegionsPage /> },
      { path: "environments", element: <EnvironmentsPage /> },
      { path: "teams", element: <TeamsPage /> },
      { path: "catalog-types", element: <CatalogTypePage /> },
      { index: true, element: <RegionsPage /> },
    ],
  },
  { path: "/infrastructure/datacenters/enabled-services", element: <DatacentersEnabledServices /> },
  { path: "/infrastructure/datacenters/instances", element: <DatacentersInstances /> },
  { path: "applications", element: <Applications /> },
  { path: "*", element: <NotFound /> },
];

export default routes;
