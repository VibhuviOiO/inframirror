import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import routes from "./routes";


import DatacentersPage from "./pages/Datacenters";
import DatacentersEnabledServices from "./pages/DatacentersEnabledServices";
import DatacentersInstances from "./pages/DatacentersInstances";
import Applications from "./pages/Applications";
import ApplicationDeployments from "./pages/ApplicationDeployments";
import NotFound from "./pages/NotFound";
import SettingsPage from "./pages/Settings";
import RegionsPage from "./pages/Regions";

import ServiceOwners from "./pages/ServiceOwners";
import ServiceDependencies from "./pages/ServiceDependencies";
import EnvironmentsPage from "./pages/Environments";
import TeamsPage from "./pages/Teams";

const queryClient = new QueryClient();

const router = createBrowserRouter(routes, {
  future: {
    v7_startTransition: true,
  },
});

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <RouterProvider router={router} />
    </TooltipProvider>
  </QueryClientProvider>
);
export default App;