import Index from "./pages/Index";
import DatacentersPage from "./pages/Datacenters";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import EnvironmentsPage from "./pages/Environments";
import HostsPage from "./pages/Hosts";
import ClustersPageWithLayout from "./pages/Clusters";
import IntegrationPageWithLayout from "./pages/Integration";
import { Service } from "dockerode";
import ServicesPageWithLayout from "./pages/Services";
import PostgresOpsPageWithLayout from "./pages/PostgresOps";
import DockerOperationsPageWithLayout from "./pages/DockerOperations";

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
  { path: "/services", element: <ServicesPageWithLayout /> },
  {
    path: "/settings",
    element: <SettingsPage />,
    children: [
      { path: "regions", element: <RegionsPage /> },
      { path: "environments", element: <EnvironmentsPage /> },
      { index: true, element: <RegionsPage /> },
    ],
  },
  { path: "/docker-operations", element: <DockerOperationsPageWithLayout /> },
  { path: "/postgres-operations", element: <PostgresOpsPageWithLayout /> },
  { path: "*", element: <NotFound /> },
];

export default routes;
