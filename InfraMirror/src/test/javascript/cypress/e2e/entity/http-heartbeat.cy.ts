import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('HttpHeartbeat e2e test', () => {
  const httpHeartbeatPageUrl = '/http-heartbeat';
  const httpHeartbeatPageUrlPattern = new RegExp('/http-heartbeat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';

  let httpHeartbeat;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/http-heartbeats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/http-heartbeats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/http-heartbeats/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (httpHeartbeat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/http-heartbeats/${httpHeartbeat.id}`,
      }).then(() => {
        httpHeartbeat = undefined;
      });
    }
  });

  it('HttpHeartbeats menu should load HttpHeartbeats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('http-heartbeat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HttpHeartbeat').should('exist');
    cy.url().should('match', httpHeartbeatPageUrlPattern);
  });

  describe('HttpHeartbeat page', () => {
    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(httpHeartbeatPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details HttpHeartbeat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('httpHeartbeat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);
      });
    });
  });
});
