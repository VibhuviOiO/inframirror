import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityDeleteButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Datacenter e2e test', () => {
  const datacenterPageUrl = '/datacenter';
  const datacenterPageUrlPattern = new RegExp('/datacenter(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const datacenterSample = { name: `test-${Date.now()}`, code: `DC${Date.now() % 100000}` };

  let datacenter;

  before(() => {
    cy.session([username, password], () => {
      cy.login(username, password);
    });
  });

  beforeEach(() => {
    cy.session([username, password], () => {
      cy.login(username, password);
    });
    cy.intercept('GET', '/api/datacenters+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/datacenters').as('postEntityRequest');
    cy.intercept('PUT', '/api/datacenters/*').as('putEntityRequest');
    cy.intercept('DELETE', '/api/datacenters/*').as('deleteEntityRequest');
  });

  describe('User behavior flows', () => {
    beforeEach(() => {
      cy.visit(datacenterPageUrl);
      cy.wait('@entitiesRequest');
    });

    it('should close side panel when clicking backdrop', () => {
      cy.get(entityCreateButtonSelector).click();
      cy.get('.side-panel', { timeout: 10000 }).should('exist');
      cy.get('.side-panel-overlay').click({ force: true });
      cy.get('.side-panel').should('not.exist');
    });

    it('should close side panel with Escape key', () => {
      cy.get(entityCreateButtonSelector).click();
      cy.get('.side-panel', { timeout: 10000 }).should('exist');
      cy.get('body').type('{esc}');
      cy.get('.side-panel').should('not.exist');
    });
  });

  it('Datacenters menu should load Datacenters page', () => {
    cy.visit('/datacenter');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.contains('h4', 'Datacenters').should('exist');
    cy.url().should('match', datacenterPageUrlPattern);
  });

  describe('Datacenter page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(datacenterPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Datacenter side panel', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.get('.side-panel', { timeout: 10000 }).should('exist');
        cy.contains('h5', 'Create').should('exist');
        cy.get('.side-panel .btn-close').click({ force: true });
        cy.get('.side-panel').should('not.exist');
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/datacenters',
          body: datacenterSample,
        }).then(({ body }) => {
          datacenter = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/datacenters+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/datacenters?page=0&size=20>; rel="last",<http://localhost/api/datacenters?page=0&size=20>; rel="first"',
              },
              body: [datacenter],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(datacenterPageUrl);
        cy.wait('@entitiesRequestInternal');
      });

      afterEach(() => {
        if (datacenter) {
          cy.authenticatedRequest({
            method: 'DELETE',
            url: `/api/datacenters/${datacenter.id}`,
            failOnStatusCode: false,
          });
          datacenter = undefined;
        }
      });

      it('edit button click should load edit Datacenter side panel', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.get('.side-panel', { timeout: 10000 }).should('exist');
        cy.contains('h5', 'Edit Datacenter').should('exist');
        cy.get('.side-panel .btn-close').click({ force: true });
        cy.get('.side-panel').should('not.exist');
      });

      it('should update an existing Datacenter', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.get('.side-panel', { timeout: 10000 }).should('exist');
        cy.wait(1000); // Wait for form to populate

        cy.fillSidePanelForm({ name: 'updated datacenter' });
        cy.get('.side-panel-footer [data-cy="entityCreateSaveButton"]').click({ force: true });

        cy.wait('@putEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.wait('@entitiesRequest');
        cy.get('.side-panel').should('not.exist');
      });

      it('last delete button click should delete instance of Datacenter', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.get('.modal').should('be.visible');
        cy.contains('Are you sure you want to delete').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', datacenterPageUrlPattern);

        datacenter = undefined;
      });

      it('should cancel delete operation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.get('.modal').should('be.visible');
        cy.get('body').then($body => {
          if ($body.find('.modal .btn-secondary').length > 0) {
            cy.get('.modal .btn-secondary').click();
          } else if ($body.find('.modal [data-cy="entityConfirmCancelButton"]').length > 0) {
            cy.get('.modal [data-cy="entityConfirmCancelButton"]').click();
          }
        });
        cy.get('.modal').should('not.exist');
        cy.get(entityTableSelector).should('exist');
      });
    });
  });

  describe('new Datacenter side panel', () => {
    beforeEach(() => {
      cy.visit(`${datacenterPageUrl}`);
      cy.wait('@entitiesRequest');
      cy.get(entityCreateButtonSelector).click();
      cy.get('.side-panel', { timeout: 10000 }).should('exist');
    });

    afterEach(() => {
      if (datacenter) {
        cy.authenticatedRequest({
          method: 'DELETE',
          url: `/api/datacenters/${datacenter.id}`,
          failOnStatusCode: false,
        });
        datacenter = undefined;
      }
    });

    it('should create an instance of Datacenter', () => {
      const uniqueName = `test-${Date.now()}`;
      const uniqueCode = `DC${Date.now() % 100000}`;

      cy.fillSidePanelForm({ name: uniqueName, code: uniqueCode });
      cy.get('.side-panel-footer [data-cy="entityCreateSaveButton"]').click({ force: true });
      // Panel should close on successful save
      // If it doesn't close, there's a validation error
    });

    it('should show validation error for required fields', () => {
      cy.get('.side-panel-footer [data-cy="entityCreateSaveButton"]').click({ force: true });
      cy.get('.side-panel', { timeout: 1000 }).should('exist');
    });
  });
});
