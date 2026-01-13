import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/branding">
        <Translate contentKey="global.menu.entities.branding" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/region">
        <Translate contentKey="global.menu.entities.region" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/datacenter">
        <Translate contentKey="global.menu.entities.datacenter" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/agent">
        <Translate contentKey="global.menu.entities.agent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/audit-trail">
        <Translate contentKey="global.menu.entities.auditTrail" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/api-key">
        <Translate contentKey="global.menu.entities.apiKey" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/http-monitor">
        <Translate contentKey="global.menu.entities.httpMonitor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/http-heartbeat">
        <Translate contentKey="global.menu.entities.httpHeartbeat" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/agent-monitor">
        <Translate contentKey="global.menu.entities.agentMonitor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/agent-lock">
        <Translate contentKey="global.menu.entities.agentLock" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/instance">
        <Translate contentKey="global.menu.entities.instance" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/instance-heartbeat">
        <Translate contentKey="global.menu.entities.instanceHeartbeat" />
      </MenuItem>
      {/* ServiceInstance managed through MonitoredService */}
      {/* <MenuItem icon="asterisk" to="/service-instance">
        <Translate contentKey="global.menu.entities.serviceInstance" />
      </MenuItem> */}
      <MenuItem icon="asterisk" to="/service-heartbeat">
        <Translate contentKey="global.menu.entities.serviceHeartbeat" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/status-page">
        <Translate contentKey="global.menu.entities.statusPage" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/status-dependency">
        <Translate contentKey="global.menu.entities.statusDependency" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/monitored-service">
        <Translate contentKey="global.menu.entities.monitoredService" />
      </MenuItem>
      <MenuItem icon="plug" to="/icc">
        ICC
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
