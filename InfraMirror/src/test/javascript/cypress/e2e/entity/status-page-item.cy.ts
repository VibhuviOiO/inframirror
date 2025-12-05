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

describe('StatusPageItem e2e test', () => {
  const statusPageItemPageUrl = '/status-page-item';
  const statusPageItemPageUrlPattern = new RegExp('/status-page-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const statusPageItemSample = { itemType: 'oof bathrobe', itemId: 8178 };

  let statusPageItem;
  let statusPage;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/status-pages',
      body: {
        name: 'yowza hm',
        slug: 'exactly',
        description: 'stuff obediently joyously',
        isPublic: false,
        customDomain: 'abaft',
        logoUrl: 'whether as',
        themeColor: 'morning',
        headerText: 'bashfully idle supposing',
        footerText: 'blond vainly amongst',
        showResponseTimes: true,
        showUptimePercentage: false,
        autoRefreshSeconds: 21616,
        monitorSelection: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        isActive: true,
        isHomePage: false,
        allowedRoles: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        createdAt: '2025-12-05T04:41:04.453Z',
        updatedAt: '2025-12-04T20:46:34.246Z',
      },
    }).then(({ body }) => {
      statusPage = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/status-page-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/status-page-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/status-page-items/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/status-pages', {
      statusCode: 200,
      body: [statusPage],
    });
  });

  afterEach(() => {
    if (statusPageItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/status-page-items/${statusPageItem.id}`,
      }).then(() => {
        statusPageItem = undefined;
      });
    }
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

  it('StatusPageItems menu should load StatusPageItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('status-page-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('StatusPageItem').should('exist');
    cy.url().should('match', statusPageItemPageUrlPattern);
  });

  describe('StatusPageItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(statusPageItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create StatusPageItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/status-page-item/new$'));
        cy.getEntityCreateUpdateHeading('StatusPageItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPageItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/status-page-items',
          body: {
            ...statusPageItemSample,
            statusPage,
          },
        }).then(({ body }) => {
          statusPageItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/status-page-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/status-page-items?page=0&size=20>; rel="last",<http://localhost/api/status-page-items?page=0&size=20>; rel="first"',
              },
              body: [statusPageItem],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(statusPageItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details StatusPageItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('statusPageItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPageItemPageUrlPattern);
      });

      it('edit button click should load edit StatusPageItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusPageItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPageItemPageUrlPattern);
      });

      it('edit button click should load edit StatusPageItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('StatusPageItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPageItemPageUrlPattern);
      });

      it('last delete button click should delete instance of StatusPageItem', () => {
        cy.intercept('GET', '/api/status-page-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('statusPageItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', statusPageItemPageUrlPattern);

        statusPageItem = undefined;
      });
    });
  });

  describe('new StatusPageItem page', () => {
    beforeEach(() => {
      cy.visit(`${statusPageItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('StatusPageItem');
    });

    it('should create an instance of StatusPageItem', () => {
      cy.get(`[data-cy="itemType"]`).type('who heating sans');
      cy.get(`[data-cy="itemType"]`).should('have.value', 'who heating sans');

      cy.get(`[data-cy="itemId"]`).type('16668');
      cy.get(`[data-cy="itemId"]`).should('have.value', '16668');

      cy.get(`[data-cy="displayOrder"]`).type('3983');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '3983');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T11:47');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T11:47');

      cy.get(`[data-cy="statusPage"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        statusPageItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', statusPageItemPageUrlPattern);
    });
  });
});
