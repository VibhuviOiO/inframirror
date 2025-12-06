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

describe('Branding e2e test', () => {
  const brandingPageUrl = '/branding';
  const brandingPageUrlPattern = new RegExp('/branding(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const brandingSample = { title: 'sticky pfft', isActive: false };

  let branding;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/brandings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/brandings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/brandings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (branding) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/brandings/${branding.id}`,
      }).then(() => {
        branding = undefined;
      });
    }
  });

  it('Brandings menu should load Brandings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('branding');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Branding').should('exist');
    cy.url().should('match', brandingPageUrlPattern);
  });

  describe('Branding page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(brandingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Branding page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/branding/new$'));
        cy.getEntityCreateUpdateHeading('Branding');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/brandings',
          body: brandingSample,
        }).then(({ body }) => {
          branding = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/brandings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/brandings?page=0&size=20>; rel="last",<http://localhost/api/brandings?page=0&size=20>; rel="first"',
              },
              body: [branding],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(brandingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Branding page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('branding');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandingPageUrlPattern);
      });

      it('edit button click should load edit Branding page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Branding');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandingPageUrlPattern);
      });

      it('edit button click should load edit Branding page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Branding');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandingPageUrlPattern);
      });

      it('last delete button click should delete instance of Branding', () => {
        cy.intercept('GET', '/api/brandings/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('branding').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandingPageUrlPattern);

        branding = undefined;
      });
    });
  });

  describe('new Branding page', () => {
    beforeEach(() => {
      cy.visit(`${brandingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Branding');
    });

    it('should create an instance of Branding', () => {
      cy.get(`[data-cy="title"]`).type('pro why');
      cy.get(`[data-cy="title"]`).should('have.value', 'pro why');

      cy.get(`[data-cy="description"]`).type('terrorise usable');
      cy.get(`[data-cy="description"]`).should('have.value', 'terrorise usable');

      cy.get(`[data-cy="keywords"]`).type('properly ew');
      cy.get(`[data-cy="keywords"]`).should('have.value', 'properly ew');

      cy.get(`[data-cy="author"]`).type('but scrap rue');
      cy.get(`[data-cy="author"]`).should('have.value', 'but scrap rue');

      cy.get(`[data-cy="faviconPath"]`).type('valiantly unethically');
      cy.get(`[data-cy="faviconPath"]`).should('have.value', 'valiantly unethically');

      cy.get(`[data-cy="logoPath"]`).type('painfully cop interviewer');
      cy.get(`[data-cy="logoPath"]`).should('have.value', 'painfully cop interviewer');

      cy.get(`[data-cy="logoWidth"]`).type('10413');
      cy.get(`[data-cy="logoWidth"]`).should('have.value', '10413');

      cy.get(`[data-cy="logoHeight"]`).type('29270');
      cy.get(`[data-cy="logoHeight"]`).should('have.value', '29270');

      cy.get(`[data-cy="footerTitle"]`).type('aha catch');
      cy.get(`[data-cy="footerTitle"]`).should('have.value', 'aha catch');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2025-12-04T08:30');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-12-04T08:30');

      cy.get(`[data-cy="updatedAt"]`).type('2025-12-05T04:21');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-12-05T04:21');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        branding = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', brandingPageUrlPattern);
    });
  });
});
