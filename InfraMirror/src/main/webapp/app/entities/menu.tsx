import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/region">
        <Translate contentKey="global.menu.entities.region" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/datacenter">
        <Translate contentKey="global.menu.entities.datacenter" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/agent">
        <Translate contentKey="global.menu.entities.agent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/instance">
        <Translate contentKey="global.menu.entities.instance" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/schedule">
        <Translate contentKey="global.menu.entities.schedule" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/http-monitor">
        <Translate contentKey="global.menu.entities.httpMonitor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/http-heartbeat">
        <Translate contentKey="global.menu.entities.httpHeartbeat" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ping-heartbeat">
        <Translate contentKey="global.menu.entities.pingHeartbeat" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/api-key">
        <Translate contentKey="global.menu.entities.apiKey" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/audit-trail">
        <Translate contentKey="global.menu.entities.auditTrail" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/tag">
        <Translate contentKey="global.menu.entities.tag" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/session-log">
        <Translate contentKey="global.menu.entities.sessionLog" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
