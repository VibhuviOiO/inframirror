import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('SessionLog e2e test', () => {
  const sessionLogPageUrl = '/session-log';
  const sessionLogPageUrlPattern = new RegExp('/session-log(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';

  let sessionLog;
  // let instance;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/instances',
      body: {"name":"till usefully","hostname":"dazzling","description":"bah","instanceType":"amongst","monitoringType":"galoshes nougat","operatingSystem":"oily floodlight kissingly","platform":"or overproduce SUV","privateIpAddress":"bah","publicIpAddress":"orientate healthily","tags":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","pingEnabled":true,"pingInterval":20231,"pingTimeoutMs":9553,"pingRetryCount":24124,"hardwareMonitoringEnabled":true,"hardwareMonitoringInterval":23885,"cpuWarningThreshold":11775,"cpuDangerThreshold":30643,"memoryWarningThreshold":5558,"memoryDangerThreshold":23257,"diskWarningThreshold":5886,"diskDangerThreshold":20926,"createdAt":"2025-12-02T10:40:01.649Z","updatedAt":"2025-12-03T02:06:15.551Z","lastPingAt":"2025-12-02T16:54:51.309Z","lastHardwareCheckAt":"2025-12-02T10:49:11.151Z"},
    }).then(({ body }) => {
      instance = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"id":"f30e94e5-33ac-4a30-ac01-e34819a0886a","login":"0","firstName":"Ernest","lastName":"Beier","email":"Travis23@gmail.com","imageUrl":"what","langKey":"nor"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/session-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/session-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/session-logs/*').as('deleteEntityRequest');
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

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (sessionLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/session-logs/${sessionLog.id}`,
      }).then(() => {
        sessionLog = undefined;
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
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('SessionLogs menu should load SessionLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('session-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SessionLog').should('exist');
    cy.url().should('match', sessionLogPageUrlPattern);
  });

  describe('SessionLog page', () => {
    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(sessionLogPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details SessionLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sessionLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', sessionLogPageUrlPattern);
      });
    });
  });
});
