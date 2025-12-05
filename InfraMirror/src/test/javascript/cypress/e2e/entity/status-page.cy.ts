import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('StatusPage e2e test', () => {
  const statusPagePageUrl = '/status-page';
  const statusPagePageUrlPattern = new RegExp('/status-page(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const statusPageSample = {
    name: 'now whoa inferior',
    slug: 'boo whereas woeful',
    isPublic: false,
    createdAt: '2025-12-04T15:08:23.945Z',
    updatedAt: '2025-12-04T06:40:57.198Z',
  };

  let statusPage;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/status-pages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/status-pages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/status-pages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (statusPage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/status-pages/${statusPage.id}`,
      }).then(() => {
        statusPage = undefined;
      });
    }
  });

  it('StatusPages menu should load StatusPages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('status-page');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StatusPage').should('exist');
    cy.url().should('match', statusPagePageUrlPattern);
  });

  describe('StatusPage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statusPagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StatusPage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/status-page/new$'));
        cy.getEntityCreateUpdateHeading('StatusPage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/status-pages',
          body: statusPageSample,
        }).then(({ body }) => {
          statusPage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/status-pages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/status-pages?page=0&size=20>; rel="last",<http://localhost/api/status-pages?page=0&size=20>; rel="first"',
              },
              body: [statusPage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(statusPagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StatusPage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('statusPage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPagePageUrlPattern);
      });

      it('edit button click should load edit StatusPage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusPage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPagePageUrlPattern);
      });

      it('edit button click should load edit StatusPage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusPage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPagePageUrlPattern);
      });

      it('last delete button click should delete instance of StatusPage', () => {
        cy.intercept('GET', '/api/status-pages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('statusPage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPagePageUrlPattern);

        statusPage = undefined;
      });
    });
  });

  describe('new StatusPage page', () => {
    beforeEach(() => {
      cy.visit(`${statusPagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StatusPage');
    });

    it('should create an instance of StatusPage', () => {
      cy.get(`[data-cy="name"]`).type('enlightened finally slide');
      cy.get(`[data-cy="name"]`).should('have.value', 'enlightened finally slide');

      cy.get(`[data-cy="slug"]`).type('sham yuck abnormally');
      cy.get(`[data-cy="slug"]`).should('have.value', 'sham yuck abnormally');

      cy.get(`[data-cy="description"]`).type('rationalise wonderfully wholly');
      cy.get(`[data-cy="description"]`).should('have.value', 'rationalise wonderfully wholly');

      cy.get(`[data-cy="isPublic"]`).should('not.be.checked');
      cy.get(`[data-cy="isPublic"]`).click();
      cy.get(`[data-cy="isPublic"]`).should('be.checked');

      cy.get(`[data-cy="customDomain"]`).type('squid');
      cy.get(`[data-cy="customDomain"]`).should('have.value', 'squid');

      cy.get(`[data-cy="logoUrl"]`).type('nervous');
      cy.get(`[data-cy="logoUrl"]`).should('have.value', 'nervous');

      cy.get(`[data-cy="themeColor"]`).type('verball');
      cy.get(`[data-cy="themeColor"]`).should('have.value', 'verball');

      cy.get(`[data-cy="headerText"]`).type('help coin');
      cy.get(`[data-cy="headerText"]`).should('have.value', 'help coin');

      cy.get(`[data-cy="footerText"]`).type('small');
      cy.get(`[data-cy="footerText"]`).should('have.value', 'small');

      cy.get(`[data-cy="showResponseTimes"]`).should('not.be.checked');
      cy.get(`[data-cy="showResponseTimes"]`).click();
      cy.get(`[data-cy="showResponseTimes"]`).should('be.checked');

      cy.get(`[data-cy="showUptimePercentage"]`).should('not.be.checked');
      cy.get(`[data-cy="showUptimePercentage"]`).click();
      cy.get(`[data-cy="showUptimePercentage"]`).should('be.checked');

      cy.get(`[data-cy="autoRefreshSeconds"]`).type('11823');
      cy.get(`[data-cy="autoRefreshSeconds"]`).should('have.value', '11823');

      cy.get(`[data-cy="monitorSelection"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="monitorSelection"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="isHomePage"]`).should('not.be.checked');
      cy.get(`[data-cy="isHomePage"]`).click();
      cy.get(`[data-cy="isHomePage"]`).should('be.checked');

      cy.get(`[data-cy="allowedRoles"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="allowedRoles"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="createdAt"]`).type('2025-12-05T03:31');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-05T03:31');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-05T02:47');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-05T02:47');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        statusPage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', statusPagePageUrlPattern);
    });
  });
});
