import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('PingHeartbeat e2e test', () => {
  const pingHeartbeatPageUrl = '/ping-heartbeat';
  const pingHeartbeatPageUrlPattern = new RegExp('/ping-heartbeat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';

  let pingHeartbeat;
  // let instance;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/instances',
      body: {"name":"brr fast","hostname":"for rusty readily","description":"jubilant","instanceType":"drat likewise debit","monitoringType":"recklessly where","operatingSystem":"majestic","platform":"vengeful outrageous finally","privateIpAddress":"brush general","publicIpAddress":"overdue yippee divine","tags":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","pingEnabled":false,"pingInterval":27770,"pingTimeoutMs":25617,"pingRetryCount":20100,"hardwareMonitoringEnabled":true,"hardwareMonitoringInterval":12567,"cpuWarningThreshold":27426,"cpuDangerThreshold":8618,"memoryWarningThreshold":29010,"memoryDangerThreshold":17876,"diskWarningThreshold":28190,"diskDangerThreshold":12379,"createdAt":"2025-12-02T14:50:40.818Z","updatedAt":"2025-12-03T06:16:04.273Z","lastPingAt":"2025-12-03T00:34:28.137Z","lastHardwareCheckAt":"2025-12-02T13:20:13.529Z"},
    }).then(({ body }) => {
      instance = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/ping-heartbeats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ping-heartbeats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ping-heartbeats/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/instances', {
      statusCode: 200,
      body: [instance],
    });

    cy.intercept('GET', '/api/agents', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (pingHeartbeat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ping-heartbeats/${pingHeartbeat.id}`,
      }).then(() => {
        pingHeartbeat = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (instance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/instances/${instance.id}`,
      }).then(() => {
        instance = undefined;
      });
    }
  });
   */

  it('PingHeartbeats menu should load PingHeartbeats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ping-heartbeat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PingHeartbeat').should('exist');
    cy.url().should('match', pingHeartbeatPageUrlPattern);
  });

  describe('PingHeartbeat page', () => {
    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(pingHeartbeatPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details PingHeartbeat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('pingHeartbeat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', pingHeartbeatPageUrlPattern);
      });
    });
  });
});
