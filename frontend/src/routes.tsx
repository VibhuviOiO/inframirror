import Index from "./pages/Index";
import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import EnvironmentsPage from "./pages/Environments";
import HostsPage from "./pages/Hosts";
import ClustersPageWithLayout from "./pages/Clusters";
import IntegrationPageWithLayout from "./pages/Integration";

const routes = [
  {
    path: "/",
    element: <Index />,
  },
  {
    path: "/datacenters",
    element: <DatacentersPage />,
  },
  { path: "/integrations", element: <IntegrationPageWithLayout /> },
  { path: "/hosts", element: <HostsPage /> },
  { path: "/clusters", element: <ClustersPageWithLayout /> },
  {
    path: "/settings",
    element: <SettingsPage />,
    children: [
      { path: "regions", element: <RegionsPage /> },
      { path: "environments", element: <EnvironmentsPage /> },
      { index: true, element: <RegionsPage /> },
    ],
  },
  { path: "/infrastructure/datacenters/enabled-services", element: <DatacentersEnabledServices /> },
  { path: "/infrastructure/datacenters/instances", element: <DatacentersInstances /> },
  { path: "*", element: <NotFound /> },
];

export default routes;
